package io.jexxa.jlegmed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a complete processing path (flowgraph) by concatenating filters
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface FlowGraph
{

    /**
     * Name of the FlowGraph.
     *
     * @return Name of the domain event
     */
    String value();
}