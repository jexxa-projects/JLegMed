package io.jexxa.jlegmed.plugins.generic.pipe;

import io.jexxa.jlegmed.core.pipes.InputPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated : Is replaced by functional interface IInputPipe
 */
@Deprecated(forRemoval = true)
public class CollectingInputPipe<T> implements InputPipe<T> {
    private final List<T> collectedData = new ArrayList<>();

    @Override
    public void receive(T data) {
        collectedData.add(data);
    }

    public List<T> getCollectedData() {
        return collectedData;
    }
}
