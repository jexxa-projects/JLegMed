package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.processor.Processor;

public abstract class SQLWriter<T> extends Processor<T, T> {


    protected T doProcess(T content, Context context)
    {
        var databaseProperties = context.getFilterProperties().orElseThrow(() -> new IllegalArgumentException("No valid database connection defined in properties"));
        var jdbcConnection = JDBCConnectionPool.getConnection(databaseProperties, this);


        executeCommand(jdbcConnection, content);

        return content;
    }

    protected abstract void executeCommand(JDBCConnection jdbcConnection, T element);

}
