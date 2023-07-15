package io.jexxa.jlegmed;


import io.jexxa.jlegmed.processor.Processor;
import io.jexxa.jlegmed.producer.Producer;

public final class JLegMed
{
    private Class<?> expectedData;
    private Producer producer;
    private Processor processor;

    public <T> JLegMed receive(Class<T> expectedData)
    {
        this.expectedData = expectedData;
        return this;
    }

    public <T extends Producer> JLegMed with(Class<T> clazz) {
        try {
            this.producer = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            return null;
        }
        return this;
    }

    public <T extends Processor> JLegMed andProcessWith(Class<T> clazz)
    {
        try {
            this.processor = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            return null;
        }

        return this;
    }

    public void run()
    {
        while (true) {
            run(1);
        }
    }

    public void run(int iteration)
    {
        for (int i = 0; i < iteration; ++i) {
            processor.process( producer.receive(expectedData) );
        }
    }

}
