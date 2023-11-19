package io.jexxa.jlegmed.plugins.persistence.producer;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;
import io.jexxa.jlegmed.plugins.persistence.JDBCContext;

import java.util.Properties;
import java.util.function.Consumer;

public abstract class JDBCProducer<T> extends PassiveProducer<T> {

    private Properties databaseProperties;
    private JDBCConnection jdbcConnection;

    @Override
    public void init()
    {
        this.databaseProperties = filterProperties()
                .orElseThrow(() -> new IllegalArgumentException("No database connection defined in properties -> Define a database connection in main using 'useProperties()' "))
                .properties();
        this.jdbcConnection = JDBCConnectionPool.getConnection(databaseProperties, this);
    }

    @Override
    public void produceData() {
        executeCommand();
    }

    @Override
    public void deInit() {
        jdbcConnection = null;
        databaseProperties = null;
    }

    protected abstract void executeCommand();

    protected JDBCConnection jdbcConnection(){
        return jdbcConnection.validateConnection();
    }



    public static <T> JDBCProducer<T> jdbcProducer(Consumer<JDBCContext<T>> consumer) {
        return new JDBCProducer<>() {
            @Override
            protected void executeCommand() {
                consumer.accept(new JDBCContext<>(jdbcConnection(), filterContext(), outputPipe()));
            }
        };
    }


}
