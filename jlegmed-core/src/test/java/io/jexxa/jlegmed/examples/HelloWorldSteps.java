package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.flowgraph.steps.ActiveSourceStep;
import io.jexxa.jlegmed.core.flowgraph.steps.PassiveSourceStep;
import io.jexxa.jlegmed.core.flowgraph.steps.ProcessorStep;
import io.jexxa.jlegmed.core.flowgraph.steps.SinkStep;
import io.jexxa.jlegmed.core.flowgraph.steps.StreamStep;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;

import java.util.Stack;

import static io.jexxa.jlegmed.core.flowgraph.steps.ProcessorStep.processorStep;
import static io.jexxa.jlegmed.core.flowgraph.steps.SinkStep.sinkStep;
import static io.jexxa.jlegmed.core.flowgraph.steps.StreamStep.streamStep;
import static io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors.passThrough;
import static io.jexxa.jlegmed.plugins.generic.producer.GenericProducer.emit;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.generate;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class HelloWorldSteps {
    public static final PassiveSourceStep<String> emitHello = emit("Hello");

    public static final ProcessorStep<String, String> appendWorld =
            processorStep((data) -> data + " World");


    public static final ProcessorStep<String, String>  passthrough = passThrough();

    public static final StreamStep<String, String> duplicator = streamStep(GenericProcessors.createDuplicator());

    public static <T> SinkStep<T> storeMessage(Stack<T> stack) {
        return sinkStep(stack::push);
    }

    public static final ActiveSourceStep<String> generateHello = generate("Hello").every(500, MILLISECONDS);

}
