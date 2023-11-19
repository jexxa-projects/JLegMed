package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.plugins.persistence.JDBCContext;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool.getConnection;

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

    @SuppressWarnings("unused")
    public static <T> JDBCProcessor<T> jdbcExecutor(Consumer<JDBCContext<T>> consumer) {
        return new JDBCProcessor<>() {
            @Override
            protected void executeCommand(JDBCConnection connection, T element) {
                consumer.accept(new JDBCContext<>(connection, filterContext(), outputPipe()));
            }
        };
    }

    public static <T> JDBCProcessor<T> jdbcProcessor(BiConsumer<JDBCContext<T>, T> biConsumer) {
        return new JDBCProcessor<>() {
            @Override
            protected void executeCommand(JDBCConnection connection, T data) {
                biConsumer.accept(new JDBCContext<>(connection, filterContext(), outputPipe()), data);
            }
        };
    }

    public static  <T> Consumer<JDBCContext<T>> dropTable(Class<T> table){
        return dropTable(table.getSimpleName());
    }

    public static  <T> Consumer<JDBCContext<T>> dropTable(String table){
        return jdbcContext -> jdbcContext.jdbcConnection().createTableCommand().dropTableIfExists(table);
    }

}
