package io.jexxa.jlegmed.plugins.generic;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;
import io.jexxa.jlegmed.core.flowgraph.steps.PassiveSourceStep;

import static io.jexxa.jlegmed.core.flowgraph.steps.PassiveSourceStep.passiveSourceStep;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.scheduledProducer;

public class GenericProducer {

    public static PassiveSourceStep<Integer> counter() {
        return
                passiveSourceStep(
                        context -> {
                            var stateID = "counter";
                            var currentCounter = context.state(stateID, Integer.class).orElse(0);

                            return context.updateState(stateID, currentCounter + 1);
                        }
                );
    }

    public static Integer counterOld(FilterContext context) {
        var stateID = "counter";
        var currentCounter = context.state(stateID,Integer.class).orElse(0);

        return context.updateState(stateID, currentCounter+1);
    }

    public static ActiveProducer<Integer> scheduledCounter()
    {
        return scheduledProducer(GenericProducer::counterOld);
    }


    private GenericProducer() {
        // Private constructor since this class provides just generic producer
    }
}
