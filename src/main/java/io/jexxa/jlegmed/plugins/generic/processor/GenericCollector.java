package io.jexxa.jlegmed.plugins.generic.processor;

import java.util.ArrayList;
import java.util.List;

public class GenericCollector<T> {
    private final List<T> receivedData = new ArrayList<>();

    public T collect(T data) {
        receivedData.add(data);
        return data;
    }

    public List<T> getMessages() {
        return receivedData;
    }

    public int getNumberOfReceivedMessages() {
        return receivedData.size();
    }

}
