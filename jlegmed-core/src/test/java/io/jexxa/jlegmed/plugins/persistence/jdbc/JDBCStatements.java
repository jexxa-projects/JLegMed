package io.jexxa.jlegmed.plugins.persistence.jdbc;


import io.jexxa.common.facade.jdbc.JDBCQuery;
import io.jexxa.common.facade.jdbc.builder.JDBCTableBuilder;
import io.jexxa.common.facade.jdbc.builder.SQLDataType;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static io.jexxa.jlegmed.plugins.persistence.jdbc.JDBCSessionPool.jdbcSession;
import static io.jexxa.jlegmed.plugins.persistence.jdbc.JDBCStatements.DBSchema.DB_INDEX;
import static io.jexxa.jlegmed.plugins.persistence.jdbc.JDBCStatements.DBSchema.DB_STRING_DATA;
import static org.apache.commons.lang3.ArrayUtils.toArray;

public class JDBCStatements {
    enum DBSchema {
        DB_INDEX,
        DB_STRING_DATA,

        DATABASE_READER_IT // Name of the database
    }

    private int lastForwardedIndexQueryBuilder = 0;
    private int lastForwardedIndexPreparedStatement = 0;

    synchronized public DataToBeStored insert(DataToBeStored data, FilterContext filterContext) {
        var jdbcSession = JDBCSessionPool.jdbcSession(filterContext);

        jdbcSession.command(DBSchema.class).
                insertInto(DBSchema.DATABASE_READER_IT).values(toArray((Object)data.index(), data.message()))
                .create()
                .asUpdate();

        return data;
    }

    synchronized public void readWithQueryBuilder(FilterContext filterContext, OutputPipe<DataToBeStored> outputPipe) {
        var jdbcSession = jdbcSession(filterContext);

        var latestIndex = getLatestIndex(jdbcSession);
        queryLatestDataQueryBuilder(jdbcSession, latestIndex)
                .processWith(resultSet -> forwardData(resultSet, outputPipe));
        this.lastForwardedIndexQueryBuilder = latestIndex;
    }

    synchronized public void readWithPreparedStatement(FilterContext filterContext, OutputPipe<DataToBeStored> outputPipe) {
        var jdbcSession = jdbcSession(filterContext);

        var latestIndex = getLatestIndex(jdbcSession);
        queryLatestDataPreparedStatement(jdbcSession, latestIndex)
                .processWith(resultSet -> forwardData(resultSet, outputPipe));
        this.lastForwardedIndexPreparedStatement = latestIndex;
    }

    synchronized public void bootstrapDatabase(FilterContext filterContext) {
        var jdbcSession = jdbcSession(filterContext);

        jdbcSession.autocreateDatabase(filterContext.properties());
        dropTable(jdbcSession);
        createTable(jdbcSession);
    }

    synchronized private void dropTable(JDBCSession jdbcSession) {
        jdbcSession.tableCommand(DBSchema.class)
                .dropTableIfExists(DBSchema.DATABASE_READER_IT)
                .asIgnore();
    }

    synchronized private void createTable(JDBCSession jdbcSession) {
        jdbcSession.tableCommand(DBSchema.class)
                .createTableIfNotExists(DBSchema.DATABASE_READER_IT)
                .addColumn(DB_INDEX, SQLDataType.INTEGER).addConstraint(JDBCTableBuilder.SQLConstraint.PRIMARY_KEY)
                .addColumn(DB_STRING_DATA, SQLDataType.VARCHAR)
                .create()
                .asIgnore();
    }


    private int getLatestIndex(JDBCSession jdbcSession) {
        return jdbcSession
                .query(DBSchema.class)
                .selectMax(DB_INDEX).from(DBSchema.DATABASE_READER_IT).create()
                .asInt().findFirst()
                .orElse(0);
    }

    private JDBCQuery queryLatestDataQueryBuilder(JDBCSession jdbcSession, int maxIndex) {
        return jdbcSession
            .query(DBSchema.class)
            .selectAll().from(DBSchema.DATABASE_READER_IT)
            .where(DB_INDEX).isGreaterThan(this.lastForwardedIndexQueryBuilder).and(DB_INDEX).isLessOrEqual(maxIndex)
            .create();
    }

    private JDBCQuery queryLatestDataPreparedStatement(JDBCSession jdbcSession, int maxIndex) {

        return jdbcSession.preparedStatement(
                "select * from database_reader_it where DB_INDEX > ? and DB_INDEX <= ?",
                 List.of(this.lastForwardedIndexPreparedStatement, maxIndex));
    }

    private void forwardData(ResultSet resultSet, OutputPipe<DataToBeStored> outputPipe) throws SQLException {
        var data = new DataToBeStored(resultSet.getInt(DB_INDEX.name()), resultSet.getString(DB_STRING_DATA.name()));
        outputPipe.forward(data);
    }
}
