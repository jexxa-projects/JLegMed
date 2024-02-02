package io.jexxa.jlegmed.plugins.persistence.jdbc;


import io.jexxa.common.facade.jdbc.JDBCConnection;
import io.jexxa.common.facade.jdbc.JDBCQuery;
import io.jexxa.common.facade.jdbc.builder.JDBCTableBuilder;
import io.jexxa.common.facade.jdbc.builder.SQLDataType;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static io.jexxa.jlegmed.plugins.persistence.jdbc.JDBCSessionPool.jdbcConnection;
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
        var jdbcConnection = JDBCSessionPool.jdbcConnection(filterContext);

        jdbcConnection.createCommand(DBSchema.class).
                insertInto(DBSchema.DATABASE_READER_IT).values(toArray((Object)data.index(), data.message()))
                .create()
                .asUpdate();

        return data;
    }

    synchronized public void readWithQueryBuilder(FilterContext filterContext, OutputPipe<DataToBeStored> outputPipe) {
        var jdbcConnection = jdbcConnection(filterContext);

        var latestIndex = getLatestIndex(jdbcConnection);
        queryLatestDataQueryBuilder(jdbcConnection, latestIndex)
                .processWith(resultSet -> forwardData(resultSet, outputPipe));
        this.lastForwardedIndexQueryBuilder = latestIndex;
    }

    synchronized public void readWithPreparedStatement(FilterContext filterContext, OutputPipe<DataToBeStored> outputPipe) {
        var jdbcConnection = jdbcConnection(filterContext);

        var latestIndex = getLatestIndex(jdbcConnection);
        queryLatestDataPreparedStatement(jdbcConnection, latestIndex)
                .processWith(resultSet -> forwardData(resultSet, outputPipe));
        this.lastForwardedIndexPreparedStatement = latestIndex;
    }

    synchronized public void bootstrapDatabase(FilterContext filterContext) {
        var jdbcConnection = jdbcConnection(filterContext);

        createDatabase(filterContext, jdbcConnection);
        dropTable(jdbcConnection);
        createTable(jdbcConnection);
    }

    synchronized private void dropTable(JDBCConnection jdbcConnection) {
        jdbcConnection.createTableCommand(DBSchema.class)
                .dropTableIfExists(DBSchema.DATABASE_READER_IT)
                .asIgnore();
    }

    synchronized private void createTable(JDBCConnection jdbcConnection) {
        jdbcConnection.createTableCommand(DBSchema.class)
                .createTableIfNotExists(DBSchema.DATABASE_READER_IT)
                .addColumn(DB_INDEX, SQLDataType.INTEGER).addConstraint(JDBCTableBuilder.SQLConstraint.PRIMARY_KEY)
                .addColumn(DB_STRING_DATA, SQLDataType.VARCHAR)
                .create()
                .asIgnore();
    }

    synchronized private void createDatabase(FilterContext jdbcContext, JDBCConnection jdbcConnection) {
        jdbcConnection.autocreateDatabase(jdbcContext.properties());
    }

    private int getLatestIndex(JDBCConnection jdbcConnection) {
        return jdbcConnection
                .createQuery(DBSchema.class)
                .selectMax(DB_INDEX).from(DBSchema.DATABASE_READER_IT).create()
                .asInt().findFirst()
                .orElse(0);
    }

    private JDBCQuery queryLatestDataQueryBuilder(JDBCConnection jdbcConnection, int maxIndex) {
        return jdbcConnection
            .createQuery(DBSchema.class)
            .selectAll().from(DBSchema.DATABASE_READER_IT)
            .where(DB_INDEX).isGreaterThan(this.lastForwardedIndexQueryBuilder).and(DB_INDEX).isLessOrEqual(maxIndex)
            .create();
    }

    private JDBCQuery queryLatestDataPreparedStatement(JDBCConnection jdbcConnection, int maxIndex) {

        return new JDBCQuery(() -> jdbcConnection,
                "select * from database_reader_it where DB_INDEX > ? and DB_INDEX <= ?",
                 List.of(this.lastForwardedIndexPreparedStatement, maxIndex));
    }

    private void forwardData(ResultSet resultSet, OutputPipe<DataToBeStored> outputPipe) throws SQLException {
        var data = new DataToBeStored(resultSet.getInt(DB_INDEX.name()), resultSet.getString(DB_STRING_DATA.name()));
        outputPipe.forward(data);
    }
}
