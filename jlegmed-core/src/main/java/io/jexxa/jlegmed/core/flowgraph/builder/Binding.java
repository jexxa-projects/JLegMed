package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.common.facade.utils.properties.PropertiesUtils;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import static io.jexxa.jlegmed.core.filter.processor.Processor.consumer;

public class Binding<T, U> {

    private final Filter filter;
    private final FlowGraph flowGraph;
    private final OutputPipe<U> outputPipe;
    private final OutputPipe<ProcessingError<T>> errorPipe;

    public Binding(Filter filter, OutputPipe<ProcessingError<T>> errorPipe, OutputPipe<U> outputPipe, FlowGraph flowGraph) {
        this.filter = filter;
        this.flowGraph = flowGraph;
        this.errorPipe = errorPipe;
        this.outputPipe = outputPipe;
        setDefaultProperties();
    }

    public Binding<T, U> useProperties(String propertiesPrefix) {
        var properties = PropertiesUtils.getSubset(flowGraph.properties(), propertiesPrefix);
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("Provided properties prefix " + propertiesPrefix + " is empty!");
        }
        filter.useProperties(new FilterProperties(propertiesPrefix, properties));

        return this;
    }

    public Binding<T, U> withoutProperties() {
        filter.noPropertiesRequired();

        return this;
    }

    public Binding<T, U> onError(SerializableConsumer<ProcessingError<T>> errorHandler)
    {
        var errorProcessor = consumer(errorHandler);
        errorPipe.connectTo(errorProcessor.inputPipe());
        return this;
    }

    public ProcessorBuilder<U> and() {
        return new ProcessorBuilder<>(outputPipe, flowGraph);
    }

    private void setDefaultProperties()
    {
        if (!filter.defaultPropertiesName().isEmpty())
        {
            var defaultProperties = PropertiesUtils.getSubset(flowGraph.properties(), filter.defaultPropertiesName());
            if (!defaultProperties.isEmpty()) {
                filter.useProperties(new FilterProperties(filter.defaultPropertiesName(), defaultProperties));
            }
        }
    }

}
