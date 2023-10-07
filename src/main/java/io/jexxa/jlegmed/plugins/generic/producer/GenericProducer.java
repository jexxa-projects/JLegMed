package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.Producer;

public class GenericProducer implements Producer {
    private int counter = 0;
    public <T> T produce(Class<T> clazz)
    {
        try {
            ++counter;
            return clazz.getDeclaredConstructor(int.class).newInstance(counter);
        } catch (Exception e){
            return null;
        }
    }
}
