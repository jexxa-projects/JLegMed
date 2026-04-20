package io.jexxa.jlegmed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents data transported in between Filters that is not published.
 * Since filters are replaceable, this represents a contract between filters
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface FlowContract
{

}