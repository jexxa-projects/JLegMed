package io.jexxa.jlegmed.plugins.persistence.repository;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Converts arbitrary repository keys into a stable unique String.
 * <p>
 * Supports:
 * - String
 * - UUID
 * - Number
 * - Enum
 * - Java Records
 * - Custom classes via toString()
 * <p>
 * Goal:
 * K -> stable identifier String
 */
public final class KeyIdentifier
{
    private KeyIdentifier()
    {
    }

    public static <K> String of(K key)
    {
        switch (key) {
            case null -> throw new IllegalArgumentException("key must not be null");
            case String value -> {
                return "String:" + value;
            }
            case UUID value -> {
                return "UUID:" + value;
            }
            case Number value -> {
                return key.getClass().getSimpleName() + ":" + value;
            }
            case Enum<?> value -> {
                return value.getDeclaringClass().getName() + ":" + value.name();
            }
            default -> {
            }
        }

        if (key.getClass().isRecord())
        {
            return recordIdentifier(key);
        }

        return key.getClass().getName() + ":" + key.toString();
    }

    private static <K> String recordIdentifier(K key)
    {
        try
        {
            var type = key.getClass();

            String values = Arrays.stream(type.getRecordComponents())
                    .sorted(Comparator.comparing(RecordComponent::getName))
                    .map(component -> readComponent(component, key))
                    .collect(Collectors.joining(","));

            return type.getName() + ":" + values;
        }
        catch (Exception exception)
        {
            throw new IllegalStateException(
                    "Could not create identifier for record key: " + key.getClass().getName(),
                    exception);
        }
    }

    private static String readComponent(RecordComponent component, Object key)
    {
        try
        {
            Object value = component.getAccessor().invoke(key);
            return component.getName() + "=" + value;
        }
        catch (Exception exception)
        {
            throw new IllegalStateException(exception);
        }
    }
}