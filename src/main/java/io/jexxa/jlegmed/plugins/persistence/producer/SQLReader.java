package io.jexxa.jlegmed.plugins.persistence.producer;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCQuery;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.sql.ResultSet;
import java.util.Properties;

public abstract class SQLReader<T> extends Producer<T> {

    private Properties databaseProperties;
    private JDBCConnection jdbcConnection;

    @Override
    public void start() {
        this.databaseProperties = getFilterProperties().orElseThrow(() -> new IllegalArgumentException("No valid database connection defined in properties"));
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
        var query = getQuery(jdbcConnection);
        var streamResult = query.as(this::readAs);
        streamResult.forEach( element -> getOutputPipe().forward(element, getContext()));
    }

    protected abstract JDBCQuery getQuery(JDBCConnection jdbcConnection);

    protected abstract T readAs(ResultSet resultSet);
}
