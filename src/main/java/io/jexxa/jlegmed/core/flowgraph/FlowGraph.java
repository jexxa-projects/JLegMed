package io.jexxa.jlegmed.core.flowgraph;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface FlowGraph {

    <U, V> TypedConnector<V> andProcessWith(BiFunction<U, Context, V> processor);
    <U, V> TypedConnector<V> andProcessWith(Function<U,V> function);
    <U> FlowGraph useConfig(U configuration);

    void start();
    void stop();

    void processMessage(Content content);

    Context getContext();
}
