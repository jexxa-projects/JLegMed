package io.jexxa.jlegmed.plugins.html.producer;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.producer.ProducerURL;
import io.jexxa.jlegmed.core.producer.TypedProducer;

public class HTMLReaderURL implements ProducerURL {

    private final String url;
    private FlowGraph flowGraph;

    public HTMLReaderURL(String url) {
        this.url = url;
    }

    @Override
    public <T> void init(TypedProducer<T> producer) {
        var htmlProducer = new HTMLProducer(url);
        producer.generatedWith(htmlProducer::produce);
    }

    public FlowGraph asJson() {
        return flowGraph;
    }

    public static HTMLReaderURL httpURL(String url) {
        return new HTMLReaderURL(url);
    }
}
