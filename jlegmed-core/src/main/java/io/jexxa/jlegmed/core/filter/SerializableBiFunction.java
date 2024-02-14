package io.jexxa.jlegmed.core.filter;

import java.io.Serializable;
import java.util.function.BiFunction;

@FunctionalInterface
public interface SerializableBiFunction<U, V, R> extends BiFunction<U, V, R>, Serializable { }
