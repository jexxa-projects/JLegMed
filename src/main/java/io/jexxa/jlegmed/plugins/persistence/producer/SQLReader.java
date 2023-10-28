package io.jexxa.jlegmed.plugins.persistence.producer;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCQuery;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public abstract class SQLReader<T> extends Producer<T> {

    private Properties databaseProperties;
    private JDBCConnection jdbcConnection;

    @Override
    public void start() {
        this.databaseProperties = getProperties()
                .orElseThrow(() -> new IllegalArgumentException("No database connection defined in properties"))
                .properties();
        this.jdbcConnection = JDBCConnectionPool.getConnection(databaseProperties, this);

        readData();
    }

    @Override
    public void stop() {
        jdbcConnection = null;
        databaseProperties = null;
    }

    protected void readData()
    {
        getQuery(jdbcConnection)
                .as(this::readAs)
                .forEach( element -> getOutputPipe().forward(element, getContext()));
    }


    protected abstract JDBCQuery getQuery(JDBCConnection jdbcConnection);

    protected abstract T readAs(ResultSet resultSet) throws SQLException;
}
