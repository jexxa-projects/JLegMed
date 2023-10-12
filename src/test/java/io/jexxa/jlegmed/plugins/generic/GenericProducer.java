package io.jexxa.jlegmed.plugins.generic;

import io.jexxa.jlegmed.core.Context;
import io.jexxa.jlegmed.dto.incoming.NewContract;

import static io.jexxa.jlegmed.core.Context.contextID;

public class GenericProducer {

    public static Integer counter(Context context) {
        var contextID = contextID(GenericProducer.class, "counter");
        var currentCounter = context.get(contextID,Integer.class).orElse(0);

        return context.update(contextID, currentCounter+1);
    }

    public static NewContract newContract(Context context) {
        var contextID = contextID(GenericProducer.class, "contractCounter");
        var currentCounter = context.get(contextID,Integer.class).orElse(1);

        return new NewContract( context.update(contextID, currentCounter+1) );
    }

    private GenericProducer() { }
}
