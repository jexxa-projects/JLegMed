package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.common.facade.jdbc.JDBCConnection;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.plugins.persistence.JDBCContext;

import java.util.function.Consumer;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getConnection;

public abstract class JDBCProcessor<T> extends Processor<T, T> {

    @Override
    public void init()
    {
        super.init();

        if (properties().isEmpty())
        {
            throw new IllegalArgumentException("No properties for database connection defined -> Define properties of SQLWriter in your main");
        }

        //Validate if connection can be established
        jdbcConnection();
    }

    protected JDBCConnection jdbcConnection()
    {
        return getConnection(properties(), this).validateConnection();
    }

    @Override
    protected T doProcess(T data, FilterContext context)
    {
        executeCommand(jdbcConnection(), data);

        return data;
    }

    protected abstract void executeCommand(JDBCConnection jdbcConnection, T element);

    public static <T> JDBCProcessor<T> jdbcExecutor(Consumer<JDBCContext<T>> consumer) {
        return new JDBCProcessor<>() {
            @Override
            protected void executeCommand(JDBCConnection connection, T element) {
                consumer.accept(new JDBCContext<>(connection, filterContext(), outputPipe()));
            }
        };
    }


}
