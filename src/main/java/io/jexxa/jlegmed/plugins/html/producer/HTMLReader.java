package io.jexxa.jlegmed.plugins.html.producer;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;
import kong.unirest.Unirest;

public class HTMLReader<T> extends TypedProducer<T> {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_TYPE = "application/json";
    private final String url;

    public HTMLReader(String url) {
        this.url = url;
    }


    @Override
    protected T doProduce(Context context) {

        return Unirest.get(url)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(getType()).getBody();
    }

    public static <T> HTMLReader<T> httpURL(String url) {
        return new HTMLReader<>(url);
    }
}
