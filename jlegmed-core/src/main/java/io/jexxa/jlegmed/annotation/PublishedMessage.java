package io.jexxa.jlegmed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents a value that must be published e.g., to a legacy system, but no more
 * context is available to use @DomainEvent or @TelemetryData
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface PublishedMessage
{

}