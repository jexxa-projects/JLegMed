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
    private final OutputPipe<T> outputPipe;
    private final OutputPipe<ProcessingError<U>> errorPipe;

    public Binding(Filter filter, OutputPipe<T> outputPipe, OutputPipe<ProcessingError<U>> errorPipe, FlowGraph flowGraph) {
        this.filter = filter;
        this.flowGraph = flowGraph;
        this.outputPipe = outputPipe;
        this.errorPipe = errorPipe;
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

    public Binding<T, U> onError(SerializableConsumer<ProcessingError<U>> errorHandler)
    {
        var errorProcessor = consumer(errorHandler);
        errorPipe.connectTo(errorProcessor.inputPipe());
        return this;
    }

    public ProcessorBuilder<T> and() {
        return new ProcessorBuilder<>(outputPipe, flowGraph);
    }
}
