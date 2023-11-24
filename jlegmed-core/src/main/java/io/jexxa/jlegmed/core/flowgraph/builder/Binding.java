package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.commons.wrapper.utils.properties.PropertiesUtils;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public class Binding<T> {

    private final Filter filter;
    private final FlowGraph flowGraph;
    private final OutputPipe<T> outputPipe;

    public Binding(Filter filter, OutputPipe<T> outputPipe, FlowGraph flowGraph) {
        this.filter = filter;
        this.flowGraph = flowGraph;
        this.outputPipe = outputPipe;
    }

    public Binding<T> useProperties(String propertiesPrefix) {
        var properties = PropertiesUtils.getSubset(flowGraph.properties(), propertiesPrefix);
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("Provided properties prefix " + propertiesPrefix + " is empty!");
        }
        filter.useProperties(new FilterProperties(propertiesPrefix, properties));

        return this;
    }

    public ProcessorBuilder<T> and() {
        return new ProcessorBuilder<>(outputPipe, flowGraph);
    }
}
