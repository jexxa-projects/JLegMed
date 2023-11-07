package io.jexxa.jlegmed.common.wrapper.utils.function;


@FunctionalInterface
public interface ThrowingBiFunction<U, V, R, E extends Exception> {
    R apply(U u, V v) throws E;
}

