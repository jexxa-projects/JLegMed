package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.core.Message;
import io.jexxa.jlegmed.core.Processor;

import java.util.ArrayList;
import java.util.List;

public class MessageCollector implements Processor {
    private final List<Message> messageList = new ArrayList<>();
    @Override
    public Message process(Message data) {
        messageList.add(data);
        return data;
    }

    public <T> List<T> getMessages(Class<T> clazz) {
        return messageList.stream()
                .map(element -> clazz.cast(element.getData()))
                .toList();
    }

    public int getNumberOfReceivedMessages() {
        return messageList.size();
    }
}
