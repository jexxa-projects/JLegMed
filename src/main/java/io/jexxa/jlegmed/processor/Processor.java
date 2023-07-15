package io.jexxa.jlegmed.processor;

public interface Processor {
    <T> T process(T data);
}
