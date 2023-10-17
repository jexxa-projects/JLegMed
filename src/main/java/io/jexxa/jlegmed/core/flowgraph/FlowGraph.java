package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.AbstractFlowGraph;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface FlowGraph {

    <U, V> FlowGraph andProcessWith(BiFunction<U, Context, V> processor);
    <U, V> FlowGraph andProcessWith(Function<U,V> function);
    <T> AbstractFlowGraph useConfig(T configuration);


    void start();
    void stop();

    void processMessage(Content content);

    Context getContext();
}
