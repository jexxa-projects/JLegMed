package io.jexxa.jlegmed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents a measured value such as the temperature that is published
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface TelemetryData
{

}