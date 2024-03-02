package io.jexxa.jlegmed.core.filter;

import java.util.Objects;
import java.util.Properties;

public record FilterProperties(String name, Properties properties) {
    public FilterProperties {
        Objects.requireNonNull(name);
        Objects.requireNonNull(properties);
    }

    public static FilterProperties filterPropertiesOf(String name, Properties properties) {
        return new FilterProperties(name, properties);
    }
}
