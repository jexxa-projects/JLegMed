package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.flowgraph.builder.ProcessorStep;
import io.jexxa.jlegmed.core.flowgraph.builder.SinkStep;
import io.jexxa.jlegmed.core.flowgraph.builder.SourceStep;
import io.jexxa.jlegmed.core.flowgraph.builder.StreamStep;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;

import java.util.Stack;

import static io.jexxa.jlegmed.core.flowgraph.builder.ProcessorStep.processorStep;
import static io.jexxa.jlegmed.core.flowgraph.builder.SinkStep.sinkStep;
import static io.jexxa.jlegmed.core.flowgraph.builder.SourceStep.sourceStep;
import static io.jexxa.jlegmed.core.flowgraph.builder.StreamStep.streamStep;
import static io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors.createPassThroughProcessor;

public class HelloWorldSteps {
    public static final SourceStep<String> generateHello =
            sourceStep(() -> "Hello");

    public static final ProcessorStep<String, String> appendWorld =
            processorStep((data) -> data + " World");


    public static final ProcessorStep<String, String>  passthrough = createPassThroughProcessor();

    public static final StreamStep<String, String> duplicator = streamStep(GenericProcessors.createDuplicator());

    public static <T> SinkStep<T> storeMessage(Stack<T> stack) {
        return sinkStep(stack::push);
    }

}
