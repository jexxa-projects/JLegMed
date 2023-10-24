package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.SourceConnector;

public interface ActiveProducerURL<T> {
    Producer<T> init(SourceConnector<T> sourceConnector);
}
