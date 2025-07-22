package io.jexxa.jlegmed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents a component that executes a specific operation step within the flow graph
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Filter
{

}