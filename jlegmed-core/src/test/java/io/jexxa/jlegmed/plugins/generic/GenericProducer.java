package io.jexxa.jlegmed.plugins.generic;

import io.jexxa.jlegmed.core.filter.FilterContext;

public class GenericProducer {

    public static Integer counter(FilterContext context) {
        var stateID = "counter";
        var currentCounter = context.state(stateID,Integer.class).orElse(0);

        return context.updateState(stateID, currentCounter+1);
    }


    private GenericProducer() {
        // Private constructor since this class provides just generic producer
    }
}
