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
    public void start() {
        this.databaseProperties = properties()
                .orElseThrow(() -> new IllegalArgumentException("No database connection defined in properties"))
                .properties();
        this.jdbcConnection = JDBCConnectionPool.getConnection(databaseProperties, this);

        executeCommand();
    }

    @Override
    public void stop() {
        jdbcConnection = null;
        databaseProperties = null;
    }

    protected abstract void executeCommand();

    protected JDBCConnection getJdbcConnection(){
        return jdbcConnection;
    }



    public static <T> JDBCProducer<T> jdbcReader(Consumer<JDBCContext<T>> consumer) {
        return new JDBCProducer<>() {
            @Override
            protected void executeCommand() {
                consumer.accept(new JDBCContext<>(getJdbcConnection(), filterContext(), outputPipe()::forward));
            }
        };
    }

    public static <T> JDBCProducer<T> execute(Consumer<JDBCContext<T>> consumer) {
        return new JDBCProducer<>() {

            @Override
            protected void executeCommand() {
                consumer.accept(new JDBCContext<>(getJdbcConnection(), filterContext(), outputPipe()::forward));
            }
        };
    }

}
