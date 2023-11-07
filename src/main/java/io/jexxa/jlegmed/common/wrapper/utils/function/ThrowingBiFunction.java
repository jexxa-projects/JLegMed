package io.jexxa.jlegmed.common.wrapper.utils.function;

/**
 * This utility class allows running methods which throw a checked exception inside a java stream
 * <p>
 * Example:
 * <pre>
 * {@code}
 * Integer[] values = {1,2,3};
 * Arrays.stream(values).
 *   forEach(
 *    exceptionLogger(value -&gt; Integer.divideUnsigned(value, 0))
 * );
 * {@code}
 * </pre>
 *
 *
 * @param <T> Lambda expression
 * @param <E> Type of the exception
 */
@FunctionalInterface
public interface ThrowingBiFunction<U, V, R, E extends Exception> {
    R apply(U u, V v) throws E;

}

