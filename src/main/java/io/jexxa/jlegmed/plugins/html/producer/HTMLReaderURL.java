package io.jexxa.jlegmed.plugins.html.producer;

import io.jexxa.jlegmed.core.processor.TypedOutputPipe;
import io.jexxa.jlegmed.core.producer.ProducerURL;
import io.jexxa.jlegmed.core.producer.TypedProducer;

public class HTMLReaderURL<T> extends ProducerURL<T> {

    private final String url;
    TypedProducer<T> typedProducer;

    public HTMLReaderURL(String url) {
        this.url = url;
    }

    @Override
    public void init(TypedProducer<T> producer) {
        var htmlProducer = new HTMLProducer(url);
        producer.generatedWith(htmlProducer::produce);
        this.typedProducer = producer;
    }

    @Override
    protected TypedOutputPipe<T> getOutputPipe() {
        return typedProducer.getOutputPipe();
    }

    public TypedProducer<T> asJson() {
        return typedProducer;
    }

    public static <T> HTMLReaderURL<T> httpURL(String url) {
        return new HTMLReaderURL<>(url);
    }
}
