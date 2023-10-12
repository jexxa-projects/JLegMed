package io.jexxa.jlegmed.plugins.generic;

import java.util.ArrayList;
import java.util.List;

public class MessageCollector<T> {
    private final List<T> contentList = new ArrayList<>();

    public T collect(T data) {
        contentList.add(data);
        return data;
    }

    public List<T> getMessages() {
        return contentList;
    }

    public int getNumberOfReceivedMessages() {
        return contentList.size();
    }
}
