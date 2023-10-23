package io.jexxa.jlegmed.plugins.html.producer;

import io.jexxa.jlegmed.core.flowgraph.TypedConnector;
import io.jexxa.jlegmed.core.producer.ProducerURL;
import io.jexxa.jlegmed.core.producer.TypedProducer;

public class HTMLReaderURL<T> extends ProducerURL<T> {

    private final String url;

    public HTMLReaderURL(String url) {
        this.url = url;
    }

    @Override
    protected void doInit(TypedProducer<T> producer) {
        var htmlProducer = new HTMLProducer(url);
        producer.generatedWith(htmlProducer::produce);
    }

    public TypedConnector<T> asJson() {
        return getConnector();
    }

    public static <T> HTMLReaderURL<T> httpURL(String url) {
        return new HTMLReaderURL<>(url);
    }
}
