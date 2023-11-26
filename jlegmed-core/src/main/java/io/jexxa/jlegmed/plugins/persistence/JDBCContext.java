package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.common.facade.jdbc.JDBCConnection;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public record JDBCContext<T>(JDBCConnection jdbcConnection, FilterContext filterContext, OutputPipe<T> outputPipe) {}
