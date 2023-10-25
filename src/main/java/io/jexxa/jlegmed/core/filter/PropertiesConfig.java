package io.jexxa.jlegmed.core.filter;

public record PropertiesConfig(String properties) {
    public static PropertiesConfig properties(String propertiesPrefix)
    {
        return new PropertiesConfig(propertiesPrefix);
    }
}
