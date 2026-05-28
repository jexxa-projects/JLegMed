package io.jexxa.jlegmed.examples.plugins;

import io.jexxa.jlegmed.core.flowgraph.steps.PassiveSourceStep;
import io.jexxa.jlegmed.core.flowgraph.steps.ProcessorStep;
import io.jexxa.jlegmed.core.flowgraph.steps.SinkStep;
import io.jexxa.jlegmed.examples.contract.NewContract;
import io.jexxa.jlegmed.examples.contract.UpdatedContract;

import java.util.Stack;

import static io.jexxa.jlegmed.core.flowgraph.steps.ProcessorStep.processorStep;
import static io.jexxa.jlegmed.core.flowgraph.steps.SinkStep.sinkStep;
import static io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors.passThrough;

public class ContractSteps {
    public static final PassiveSourceStep<NewContract> contractGenerator =
            PassiveSourceStep.passiveSourceStep(ContractFilter::generateContract);

    public static final ProcessorStep<NewContract, UpdatedContract> updateContract =
            processorStep(ContractFilter::transformToUpdatedContract);

    public static final ProcessorStep<UpdatedContract, UpdatedContract> validateContract =
            passThrough();

    public static <T> PassiveSourceStep<T> readContract (Stack<T> stack) {
         return PassiveSourceStep.passiveSourceStep( stack::pop);
    }

    public static <T> SinkStep<T> storeContract(Stack<T> stack) {
        return sinkStep(stack::push);
    }


}
