package io.jexxa.jlegmed.plugins.generic.pipe;

import io.jexxa.jlegmed.core.pipes.InputPipe;

import java.util.ArrayList;
import java.util.List;

public class CollectingInputPipe<T> extends InputPipe<T> {
    private final List<T> collectedData = new ArrayList<>();
    public CollectingInputPipe() {
        super(null);
    }

    @Override
    public void receive(T data) {
        collectedData.add(data);
    }

    public List<T> getCollectedData() {
        return collectedData;
    }
}
