package io.jexxa.jlegmed.processor;

public class  ConsoleProcessor implements Processor {

    public <T> T process(T data)
    {
        System.out.println( data );
        return data;
    }

}
