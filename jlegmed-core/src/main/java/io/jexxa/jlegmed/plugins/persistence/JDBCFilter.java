package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.jlegmed.plugins.persistence.producer.JDBCProducer;

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


    private JDBCFilter()
    {
        // Private constructor since we provide only static methods
    }
}
