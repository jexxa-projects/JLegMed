package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.jlegmed.common.wrapper.jdbc.builder.JDBCTableBuilder;
import io.jexxa.jlegmed.common.wrapper.jdbc.builder.SQLDataType;

import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.persistence.DatabaseTestData.DBSchema.DB_INDEX;
import static io.jexxa.jlegmed.plugins.persistence.DatabaseTestData.DBSchema.DB_STRING_DATA;
import static org.apache.commons.lang3.ArrayUtils.toArray;

public class DatabaseTestData {
    private boolean databaseInitialized = false;
    private int lastForwardedIndex = 0;

    enum DBSchema {
        DB_INDEX,
        DB_STRING_DATA,

        DATABASE_READER_IT
    }


    synchronized public void insertTestData(JDBCContext<DatabaseReaderIT.TestData> jdbcContext, DatabaseReaderIT.TestData data) {
        initDatabaseIfRequired(jdbcContext);

        jdbcContext.jdbcConnection().createCommand(DBSchema.class).
                insertInto(DBSchema.DATABASE_READER_IT).values(toArray(data.index(), data.message()))
                .create()
                .asUpdate();
    }

    synchronized public void readTestData(JDBCContext<DatabaseReaderIT.TestData> jdbcContext) {
        initDatabaseIfRequired(jdbcContext);

        var latestIndex = getLatestIndex(jdbcContext);
        getLatestData(jdbcContext, latestIndex)
                .forEach(element -> jdbcContext.outputPipe().accept(element));
        this.lastForwardedIndex = latestIndex;
    }

    synchronized private void initDatabaseIfRequired(JDBCContext<DatabaseReaderIT.TestData> jdbcContext) {
        if (!databaseInitialized) {
            createDatabase(jdbcContext);
            createTable(jdbcContext);
            databaseInitialized = true;
        }
    }

    synchronized private void createTable(JDBCContext<DatabaseReaderIT.TestData> jdbcContext) {
        if (!databaseInitialized) {
            jdbcContext.jdbcConnection().createTableCommand(DBSchema.class)
                    .dropTableIfExists(DBSchema.DATABASE_READER_IT)
                    .asIgnore();

            jdbcContext.jdbcConnection().createTableCommand(DBSchema.class)
                    .createTableIfNotExists(DBSchema.DATABASE_READER_IT)
                    .addColumn(DB_INDEX, SQLDataType.INTEGER).addConstraint(JDBCTableBuilder.SQLConstraint.PRIMARY_KEY)
                    .addColumn(DB_STRING_DATA, SQLDataType.VARCHAR)
                    .create()
                    .asIgnore();
            databaseInitialized = true;
        }
    }

    synchronized private void createDatabase(JDBCContext<DatabaseReaderIT.TestData> jdbcContext) {
        if (!databaseInitialized) {
            jdbcContext.jdbcConnection().autocreateDatabase(jdbcContext.filterContext().filterProperties().orElseThrow().properties());
        }
    }

    private int getLatestIndex(JDBCContext<DatabaseReaderIT.TestData> jdbcContext) {
        return jdbcContext.jdbcConnection()
                .createQuery(DBSchema.class)
                .selectMax(DB_INDEX).from(DBSchema.DATABASE_READER_IT).create()
                .asInt().findFirst()
                .orElse(0);
    }

    private Stream<DatabaseReaderIT.TestData> getLatestData(JDBCContext<DatabaseReaderIT.TestData> jdbcContext, int maxIndex) {

        return jdbcContext.jdbcConnection()
                .createQuery(DBSchema.class)
                .selectAll().from(DBSchema.DATABASE_READER_IT)
                .where(DB_INDEX).isGreaterThan(this.lastForwardedIndex).and(DB_INDEX).isLessOrEqual(maxIndex)
                .create()

                .as(resultSet -> new DatabaseReaderIT.TestData(resultSet.getInt(DB_INDEX.name()), resultSet.getString(DB_STRING_DATA.name())));
    }

}
