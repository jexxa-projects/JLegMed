package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.common.facade.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public class TestRepository {

    public static TextEntity add(TextEntity textEntity, FilterContext filterContext) {
        return RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).add(textEntity);
    }

    public static void read(FilterContext filterContext, OutputPipe<TextEntity> outputPipe) {
        RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).get().forEach(outputPipe::forward);
    }

    public static void dropTable(FilterContext filterContext) {
        JDBCConnectionPool.getJDBCConnection(filterContext.properties(), filterContext)
                .tableCommand()
                .dropTableIfExists(TextEntity.class.getSimpleName())
                .asIgnore();
    }
}