package io.jexxa.jlegmed.plugins.generic;

import io.jexxa.jlegmed.core.Content;
import io.jexxa.jlegmed.core.Processor;

import java.util.ArrayList;
import java.util.List;

public class MessageCollector implements Processor {
    private final List<Content> contentList = new ArrayList<>();
    @Override
    public Content process(Content data) {
        contentList.add(data);
        return data;
    }

    public <T> List<T> getMessages(Class<T> clazz) {
        return contentList.stream()
                .map(element -> clazz.cast(element.data()))
                .toList();
    }

    public int getNumberOfReceivedMessages() {
        return contentList.size();
    }
}
