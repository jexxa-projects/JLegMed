package io.jexxa.jlegmed.plugins.generic;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.dto.incoming.NewContract;

import static io.jexxa.jlegmed.core.filter.Context.stateID;

public class GenericProducer {

    public static Integer counter(Context context) {
        var contextID = stateID(GenericProducer.class, "counter");
        var currentCounter = context.getState(contextID,Integer.class).orElse(0);

        return context.updateState(contextID, currentCounter+1);
    }

    public static NewContract newContract(Context context) {
        var contextID = stateID(GenericProducer.class, "contractCounter");
        var currentCounter = context.getState(contextID,Integer.class).orElse(1);

        return new NewContract( context.updateState(contextID, currentCounter+1) );
    }

    private GenericProducer() { }
}
