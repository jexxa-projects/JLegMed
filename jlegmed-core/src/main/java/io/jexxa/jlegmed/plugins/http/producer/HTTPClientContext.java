package io.jexxa.jlegmed.plugins.http.producer;

import io.jexxa.jlegmed.core.filter.FilterContext;
import kong.unirest.UnirestInstance;

import java.util.function.Consumer;

public record HTTPClientContext<T>(
        UnirestInstance unirest,
        FilterContext filterContext,
        Class<T> dataType,
        Consumer<T> outputPipe)
{}
