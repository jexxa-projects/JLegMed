package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.processor.Processor;

import java.util.function.Function;

public abstract class SQLWriter<T> extends Processor<T, T> {


    protected T doProcess(T content, Context context)
    {
        var databaseProperties = context.getProperties().orElseThrow(() -> new IllegalArgumentException("No valid database connection defined in properties"));
        var jdbcConnection = JDBCConnectionPool.getConnection(databaseProperties, this);


        executeCommand(jdbcConnection, content);

        return content;
    }

    protected abstract void executeCommand(JDBCConnection jdbcConnection, T element);



    public static <T> SQLWriter<T> insert(String table, Function<T, Object[]> objectMapper)
    {
        return new SQLWriter<>() {
            @Override
            protected void executeCommand(JDBCConnection jdbcConnection, T element) {
                jdbcConnection.createCommand().
                        insertInto(table).values(objectMapper.apply(element))
                        .create()
                        .asUpdate();
            }
        };
    }
}
