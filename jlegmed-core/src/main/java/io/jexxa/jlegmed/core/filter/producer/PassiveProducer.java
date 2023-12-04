package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;

import java.util.Optional;

public abstract class PassiveProducer<T> extends Producer<T> {
    private IDrivingAdapter drivingAdapter;

    public abstract void produceData();

    public void drivingAdapter(IDrivingAdapter drivingAdapter)
    {
        this.drivingAdapter = drivingAdapter;
    }

    public Optional<IDrivingAdapter> drivingAdapter()
    {
        return Optional.ofNullable(drivingAdapter);
    }
}
