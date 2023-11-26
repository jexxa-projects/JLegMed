package io.jexxa.jlegmed.plugins.persistence;


import io.jexxa.common.facade.jdbc.JDBCQuery;
import io.jexxa.common.facade.jdbc.builder.JDBCTableBuilder;
import io.jexxa.common.facade.jdbc.builder.SQLDataType;
import io.jexxa.jlegmed.core.pipes.OutputPipe;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static io.jexxa.jlegmed.plugins.persistence.JDBCStatementsForTestData.DBSchema.DB_INDEX;
import static io.jexxa.jlegmed.plugins.persistence.JDBCStatementsForTestData.DBSchema.DB_STRING_DATA;
import static org.apache.commons.lang3.ArrayUtils.toArray;

public class JDBCStatementsForTestData {
    private boolean databaseInitialized = false;
    private int lastForwardedIndexQueryBuilder = 0;
    private int lastForwardedIndexPreparedStatement = 0;

    enum DBSchema {
        DB_INDEX,
        DB_STRING_DATA,

        DATABASE_READER_IT // Name of the database
    }


    synchronized public void insertTestData(JDBCContext<TestData> jdbcContext, TestData data) {
        initDatabaseIfRequired(jdbcContext);

        jdbcContext.jdbcConnection().createCommand(DBSchema.class).
                insertInto(DBSchema.DATABASE_READER_IT).values(toArray(data.index(), data.message()))
                .create()
                .asUpdate();
    }

    synchronized public void readTestDataQueryBuilder(JDBCContext<TestData> jdbcContext) {
        initDatabaseIfRequired(jdbcContext);

        var latestIndex = getLatestIndex(jdbcContext);
        queryLatestDataQueryBuilder(jdbcContext, latestIndex)
                .processWith(resultSet -> forwardData(resultSet, jdbcContext.outputPipe()));
        this.lastForwardedIndexQueryBuilder = latestIndex;
    }

    synchronized public void readTestDataPreparedStatement(JDBCContext<TestData> jdbcContext) {
        initDatabaseIfRequired(jdbcContext);

        var latestIndex = getLatestIndex(jdbcContext);
        queryLatestDataPreparedStatement(jdbcContext, latestIndex)
                .processWith(resultSet -> forwardData(resultSet, jdbcContext.outputPipe()));
        this.lastForwardedIndexPreparedStatement = latestIndex;
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
        jdbcContext.jdbcConnection().autocreateDatabase(jdbcContext.filterContext().properties());
    }

    private int getLatestIndex(JDBCContext<TestData> jdbcContext) {
        return jdbcContext.jdbcConnection()
                .createQuery(DBSchema.class)
                .selectMax(DB_INDEX).from(DBSchema.DATABASE_READER_IT).create()
                .asInt().findFirst()
                .orElse(0);
    }

    private JDBCQuery queryLatestDataQueryBuilder(JDBCContext<TestData> jdbcContext, int maxIndex) {
        return jdbcContext.jdbcConnection()
            .createQuery(DBSchema.class)
            .selectAll().from(DBSchema.DATABASE_READER_IT)
            .where(DB_INDEX).isGreaterThan(this.lastForwardedIndexQueryBuilder).and(DB_INDEX).isLessOrEqual(maxIndex)
            .create();
    }

    private JDBCQuery queryLatestDataPreparedStatement(JDBCContext<TestData> jdbcContext, int maxIndex) {

        return new JDBCQuery(jdbcContext::jdbcConnection,
                "select * from database_reader_it where DB_INDEX > ? and DB_INDEX <= ?",
                 List.of(this.lastForwardedIndexPreparedStatement, maxIndex));
    }

    private void forwardData(ResultSet resultSet, OutputPipe<TestData> outputPipe) throws SQLException {
        var data = new TestData(resultSet.getInt(DB_INDEX.name()), resultSet.getString(DB_STRING_DATA.name()));
        outputPipe.forward(data);
    }
}
