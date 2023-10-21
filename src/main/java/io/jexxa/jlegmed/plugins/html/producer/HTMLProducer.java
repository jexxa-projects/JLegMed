package io.jexxa.jlegmed.plugins.html.producer;

import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.producer.Producer;
import kong.unirest.Unirest;

public class HTMLProducer implements Producer {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_TYPE = "application/json";

    private final String url;

    public HTMLProducer(String url)
    {
        this.url = url;
    }

    @Override
    public Object produce(Class<?> clazz, Context context) {

        return Unirest.get(url)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(clazz).getBody();
    }
}
