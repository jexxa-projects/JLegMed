package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageCollector implements Processor {
    private final List<Object> messageList = new ArrayList<>();
    @Override
    public Message process(Message data) {
        messageList.add(data);
        return data;
    }

    public int getNumberOfReceivedMessages() {
        return messageList.size();
    }
}
