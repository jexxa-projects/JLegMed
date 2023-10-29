package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.core.filter.FilterContext;

public record JDBCContext(JDBCConnection jdbcConnection, FilterContext filterContext) {
}
