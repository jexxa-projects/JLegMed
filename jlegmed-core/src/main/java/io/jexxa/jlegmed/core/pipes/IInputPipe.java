package io.jexxa.jlegmed.core.pipes;

@FunctionalInterface
public interface IInputPipe<T> {
    void receive(T data);
}
