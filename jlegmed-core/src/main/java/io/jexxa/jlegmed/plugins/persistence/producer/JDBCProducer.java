package io.jexxa.jlegmed.plugins.persistence.producer;

import io.jexxa.common.facade.jdbc.JDBCConnection;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getConnection;

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


}
