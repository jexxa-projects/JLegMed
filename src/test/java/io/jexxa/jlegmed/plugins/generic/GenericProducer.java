package io.jexxa.jlegmed.plugins.generic;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.dto.incoming.NewContract;

public class GenericProducer {

    public static Integer counter(FilterContext context) {
        var stateID = "counter";
        var currentCounter = context.state(stateID,Integer.class).orElse(0);

        return context.updateState(stateID, currentCounter+1);
    }

    public static NewContract newContract(FilterContext context) {
        var stateID = "newContract";
        var currentCounter = context.state(stateID,Integer.class).orElse(1);

        return new NewContract( context.updateState(stateID, currentCounter+1) );
    }

    private GenericProducer() { }
}
