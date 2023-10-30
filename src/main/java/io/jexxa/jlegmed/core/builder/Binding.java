package io.jexxa.jlegmed.core.builder;

import io.jexxa.jlegmed.common.wrapper.utils.properties.PropertiesUtils;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public class Binding<T> {

    private final Filter predecessor;
    private final FlowGraph<?> flowGraph;
    private final OutputPipe<T> outputPipe;

    public Binding(Filter filter, OutputPipe<T> outputPipe, FlowGraph<?> flowGraph) {
        this.predecessor = filter;
        this.flowGraph = flowGraph;
        this.outputPipe = outputPipe;
    }


    public <U> Binding<T> filterConfig(U configuration) {
        predecessor.config(configuration);
        return this;
    }

    public Binding<T> useProperties(FilterProperties filterProperties) {
        predecessor.properties(filterProperties);
        return this;
    }

    public Binding<T> useProperties(String propertiesPrefix) {
        var properties = PropertiesUtils.getSubset(flowGraph.getProperties(), propertiesPrefix);
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("Provided properties prefix " + propertiesPrefix + " is empty!");
        }
        predecessor.properties(new FilterProperties(propertiesPrefix, properties));

        return this;
    }

    @SuppressWarnings("unused")
    public <U> Binding<T> configureWith(FilterProperties filterProperties, U filterConfig) {
        filterConfig(filterConfig);
        return useProperties(filterProperties);
    }

    public <U> Binding<T> configureWith(String propertiesPrefix, U filterConfig) {
        useProperties(propertiesPrefix);
        return filterConfig(filterConfig);
    }

    public ProcessorBuilder<T> and() {
        return new ProcessorBuilder<>(outputPipe, flowGraph);
    }
}
