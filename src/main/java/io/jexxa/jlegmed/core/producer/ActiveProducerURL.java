package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.SourceConnector;

public interface ActiveProducerURL<T> {
    ActiveProducer<T> init(SourceConnector<T> sourceConnector);
}
