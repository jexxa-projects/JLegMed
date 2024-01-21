package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.common.facade.jdbc.JDBCConnection;
import io.jexxa.jlegmed.plugins.persistence.processor.JDBCProcessor;
import io.jexxa.jlegmed.plugins.persistence.producer.JDBCProducer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JDBCFilter {
    public static <T> JDBCProducer<T> jdbcProducer(Consumer<JDBCContext<T>> consumer) {
        return new JDBCProducer<>() {
            @Override
            protected void executeCommand() {
                consumer.accept(new JDBCContext<>(jdbcConnection(), filterContext(), outputPipe()));
            }
        };
    }

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


    private JDBCFilter()
    {
        // Private constructor since we provide only static methods
    }
}
