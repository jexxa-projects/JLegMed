package io.jexxa.jlegmed.plugins.generic.producer;

import java.util.stream.Stream;

public class StreamProducer {
    public static <T> ThreadedProducer<T> streamProducer(Stream<T> stream)
    {
        return new ThreadedProducer<>()
        {
            @Override
            public String name()
            {
                return "streamProducer";
            }

            @Override
            public void produceData() {
                stream.forEach(this::forwardData);
            }
        };
    }
    private StreamProducer()
    {
        //private constructor
    }
}
