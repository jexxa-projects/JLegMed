package io.jexxa.jlegmed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents a published domain event in the context of DDD.
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface DomainEvent
{

}