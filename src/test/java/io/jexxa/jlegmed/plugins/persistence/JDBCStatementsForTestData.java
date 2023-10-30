package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.jlegmed.common.wrapper.jdbc.builder.JDBCTableBuilder;
import io.jexxa.jlegmed.common.wrapper.jdbc.builder.SQLDataType;

import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.persistence.JDBCStatementsForTestData.DBSchema.DB_INDEX;
import static io.jexxa.jlegmed.plugins.persistence.JDBCStatementsForTestData.DBSchema.DB_STRING_DATA;
import static org.apache.commons.lang3.ArrayUtils.toArray;

public class JDBCStatementsForTestData {
    private boolean databaseInitialized = false;
    private int lastForwardedIndex = 0;

    enum DBSchema {
        DB_INDEX,
        DB_STRING_DATA,

        DATABASE_READER_IT
    }


    synchronized public void insertTestData(JDBCContext<TestData> jdbcContext, TestData data) {
        initDatabaseIfRequired(jdbcContext);

        jdbcContext.jdbcConnection().createCommand(DBSchema.class).
                insertInto(DBSchema.DATABASE_READER_IT).values(toArray(data.index(), data.message()))
                .create()
                .asUpdate();
    }

    synchronized public void readTestData(JDBCContext<TestData> jdbcContext) {
        initDatabaseIfRequired(jdbcContext);

        var latestIndex = getLatestIndex(jdbcContext);
        var latestData = getLatestData(jdbcContext, latestIndex);
        latestData.forEach(element -> jdbcContext.outputPipe().accept(element));
        this.lastForwardedIndex = latestIndex;
    }

    synchronized private void initDatabaseIfRequired(JDBCContext<TestData> jdbcContext) {
        if (!databaseInitialized) {
            createDatabase(jdbcContext);
            dropTable(jdbcContext);
            createTable(jdbcContext);
            databaseInitialized = true;
        }
    }

    synchronized private void dropTable(JDBCContext<TestData> jdbcContext) {
        jdbcContext.jdbcConnection().createTableCommand(DBSchema.class)
                .dropTableIfExists(DBSchema.DATABASE_READER_IT)
                .asIgnore();
    }

    synchronized private void createTable(JDBCContext<TestData> jdbcContext) {
        jdbcContext.jdbcConnection().createTableCommand(DBSchema.class)
                .createTableIfNotExists(DBSchema.DATABASE_READER_IT)
                .addColumn(DB_INDEX, SQLDataType.INTEGER).addConstraint(JDBCTableBuilder.SQLConstraint.PRIMARY_KEY)
                .addColumn(DB_STRING_DATA, SQLDataType.VARCHAR)
                .create()
                .asIgnore();
    }

    synchronized private void createDatabase(JDBCContext<TestData> jdbcContext) {
        var filterProperties = jdbcContext.filterContext().filterProperties().orElseThrow(() -> new IllegalStateException("No properties configured to access a database"));
        jdbcContext.jdbcConnection().autocreateDatabase(filterProperties.properties());
    }

    private int getLatestIndex(JDBCContext<TestData> jdbcContext) {
        return jdbcContext.jdbcConnection()
                .createQuery(DBSchema.class)
                .selectMax(DB_INDEX).from(DBSchema.DATABASE_READER_IT).create()
                .asInt().findFirst()
                .orElse(0);
    }

    private Stream<TestData> getLatestData(JDBCContext<TestData> jdbcContext, int maxIndex) {
        return jdbcContext.jdbcConnection()
                .createQuery(DBSchema.class)
                .selectAll().from(DBSchema.DATABASE_READER_IT)
                .where(DB_INDEX).isGreaterThan(this.lastForwardedIndex).and(DB_INDEX).isLessOrEqual(maxIndex)
                .create()

                .as(resultSet -> new TestData(resultSet.getInt(DB_INDEX.name()), resultSet.getString(DB_STRING_DATA.name())));
    }
}
