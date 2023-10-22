package io.jexxa.jlegmed.plugins.html.producer;

import io.jexxa.jlegmed.core.flowgraph.Context;
import kong.unirest.Unirest;

public class HTMLProducer {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_TYPE = "application/json";

    private final String url;

    public HTMLProducer(String url)
    {
        this.url = url;
    }

    public <T> T produce(Context context, Class<T> clazz) {

        return Unirest.get(url)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(clazz).getBody();
    }
}
