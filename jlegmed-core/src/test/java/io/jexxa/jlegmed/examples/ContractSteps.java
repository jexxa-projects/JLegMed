package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.flowgraph.builder.SinkStep;
import io.jexxa.jlegmed.core.flowgraph.builder.SourceStep;
import io.jexxa.jlegmed.core.flowgraph.builder.ProcessorStep;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;

import java.util.Stack;

import static io.jexxa.jlegmed.core.flowgraph.builder.SinkStep.sinkStep;
import static io.jexxa.jlegmed.core.flowgraph.builder.SourceStep.sourceStep;
import static io.jexxa.jlegmed.core.flowgraph.builder.ProcessorStep.processorStep;

public class ContractSteps {
    public static final SourceStep<ContractFilter.NewContract> contractGenerator =
            sourceStep(ContractFilter::generateContract);

    public static final ProcessorStep<ContractFilter.NewContract, ContractFilter.UpdatedContract> updateContract =
            processorStep(ContractFilter::transformToUpdatedContract);

    public static final ProcessorStep<ContractFilter.UpdatedContract, ContractFilter.UpdatedContract> validateContract =
            processorStep(GenericProcessors::idProcessor);

    public static <T> SourceStep<T> readContract (Stack<T> stack) {
         return sourceStep( stack::pop);
    }

    public static <T> SinkStep<T> storeContract(Stack<T> stack) {
        return sinkStep(stack::push);
    }
}
