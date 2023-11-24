package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.commons.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public record JDBCContext<T>(JDBCConnection jdbcConnection, FilterContext filterContext, OutputPipe<T> outputPipe) {}
