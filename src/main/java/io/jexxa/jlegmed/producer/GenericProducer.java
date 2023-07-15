package io.jexxa.jlegmed.producer;

public class GenericProducer implements Producer{
    private int counter = 0;
    public <T> T receive(Class<T> clazz)
    {
        try {
            ++counter;
            return clazz.getDeclaredConstructor(int.class).newInstance(counter);
        } catch (Exception e){
            System.out.println("error creating new instance of " + clazz.getName());
            return null;
        }
    }
}
