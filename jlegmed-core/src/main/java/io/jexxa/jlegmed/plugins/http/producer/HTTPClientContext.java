package io.jexxa.jlegmed.plugins.http.producer;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;
import kong.unirest.UnirestInstance;

public record HTTPClientContext<T>(
        UnirestInstance unirest,
        FilterContext filterContext,
        Class<T> dataType,
        OutputPipe<T> outputPipe)
{}
