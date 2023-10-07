package io.jexxa.jlegmed.producer;

import io.jexxa.jlegmed.Context;

import static io.jexxa.jlegmed.Context.idOf;

public class GenericContextProducer {

    public static <T> T produce(Class<T> clazz, Context context) {
        var contextID = idOf(GenericContextProducer.class, "produce");
        var currentCounter = context.getContextData(contextID, Integer.class, 0);

        try {
            context.updateContextData(contextID, currentCounter+1);
            return clazz.getDeclaredConstructor(int.class).newInstance(currentCounter);
        } catch (Exception e){
            return null;
        }
    }

    private GenericContextProducer()
    {

    }
}
