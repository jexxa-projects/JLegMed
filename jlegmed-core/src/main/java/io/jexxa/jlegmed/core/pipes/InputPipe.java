package io.jexxa.jlegmed.core.pipes;

@FunctionalInterface
public interface InputPipe<T> {
    void receive(T data);
}
