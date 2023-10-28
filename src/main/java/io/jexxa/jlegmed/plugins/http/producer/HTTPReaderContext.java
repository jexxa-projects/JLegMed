package io.jexxa.jlegmed.plugins.http.producer;

import kong.unirest.UnirestInstance;

import java.util.Properties;

public record HTTPReaderContext<T>(UnirestInstance unirest, Properties properties, Class<T> type) {
}
