package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;

public abstract class ActiveProducer<T> extends Producer<T> {
    public abstract IDrivingAdapter drivingAdapter();
}
