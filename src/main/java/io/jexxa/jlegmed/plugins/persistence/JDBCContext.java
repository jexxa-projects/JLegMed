package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.function.Consumer;

public record JDBCContext<T>(JDBCConnection jdbcConnection, FilterContext filterContext, Consumer<T> outputPipe) {
}
