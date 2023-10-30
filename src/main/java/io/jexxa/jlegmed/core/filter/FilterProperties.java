package io.jexxa.jlegmed.core.filter;

import java.util.Objects;
import java.util.Properties;

public record FilterProperties(String configName, Properties properties) {
    public FilterProperties {
        Objects.requireNonNull(configName);
        Objects.requireNonNull(properties);
    }
}
