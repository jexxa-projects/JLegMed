package io.jexxa.jlegmed.plugins.persistence.producer;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.persistence.JDBCContext;

import java.util.Properties;
import java.util.function.Consumer;

public abstract class JDBCProducer<T> extends Producer<T> {

    private Properties databaseProperties;
    private JDBCConnection jdbcConnection;

    @Override
    public void init()
    {
        this.databaseProperties = properties()
                .orElseThrow(() -> new IllegalArgumentException("No database connection defined in properties -> Define a database connection in main using 'useProperties()' "))
                .properties();
        this.jdbcConnection = JDBCConnectionPool.getConnection(databaseProperties, this);
    }

    @Override
    public void start() {
        executeCommand();
    }

    @Override
    public void deInit() {
        jdbcConnection = null;
        databaseProperties = null;
    }

    protected abstract void executeCommand();

    protected JDBCConnection getJdbcConnection(){
        return jdbcConnection;
    }



    public static <T> JDBCProducer<T> jdbcProducer(Consumer<JDBCContext<T>> consumer) {
        return new JDBCProducer<>() {
            @Override
            protected void executeCommand() {
                consumer.accept(new JDBCContext<>(getJdbcConnection(), filterContext(), outputPipe()));
            }
        };
    }

    @SuppressWarnings("unused")
    public static <T> JDBCProducer<T> jdbcExecutor(Consumer<JDBCContext<T>> consumer) {
        return new JDBCProducer<>() {

            @Override
            protected void executeCommand() {
                consumer.accept(new JDBCContext<>(getJdbcConnection(), filterContext(), outputPipe()));
            }
        };
    }

}
