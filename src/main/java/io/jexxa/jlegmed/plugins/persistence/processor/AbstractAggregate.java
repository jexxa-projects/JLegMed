package io.jexxa.jlegmed.plugins.persistence.processor;

import java.util.function.Function;

public interface AbstractAggregate<T, K> {
    Class<T> getAggregateType();

    Function<T,K> getKeyFunction();
}
