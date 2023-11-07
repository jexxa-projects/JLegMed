package io.jexxa.jlegmed.core.filter;

import java.util.Objects;
import java.util.Properties;

public record FilterProperties(String propertiesName, Properties properties) {
    public FilterProperties {
        Objects.requireNonNull(propertiesName);
        Objects.requireNonNull(properties);
    }

    public static FilterProperties filterPropertiesOf(String propertiesName, Properties properties) {
        return new FilterProperties(propertiesName, properties);
    }
}
