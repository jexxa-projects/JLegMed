package io.jexxa.jlegmed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents data transported between Filters.
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface FlowData
{

}