package io.jexxa.jlegmed.plugins.persistence.producer;

import io.jexxa.commons.facade.jdbc.JDBCConnection;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;
import io.jexxa.jlegmed.plugins.persistence.JDBCContext;

import java.util.function.Consumer;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.commons.facade.jdbc.JDBCConnectionPool.getConnection;

public abstract class JDBCProducer<T> extends PassiveProducer<T> {

    @Override
    public void init()
    {
        if (properties().isEmpty()) {
            throw new IllegalArgumentException("No database connection defined in properties -> Define a database connection in main using 'useProperties()' ");
        }

        //validate jdbc connection
        jdbcConnection();
    }

    @Override
    public void produceData() {
        getInvocationHandler(this).invoke(this, this::executeCommand);
    }


    protected abstract void executeCommand();

    protected JDBCConnection jdbcConnection(){
        return getConnection(properties(), this).validateConnection();
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
