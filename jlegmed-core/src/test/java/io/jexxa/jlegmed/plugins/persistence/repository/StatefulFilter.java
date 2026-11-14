package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.common.facade.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Locale;

public class StatefulFilter {

    public static TextEntity add(TextEntity textEntity, FilterContext filterContext) {
        System.out.println("> " + RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).repository().get().size());

        RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).add(textEntity);
        System.out.println("< " + RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).repository().get().size());
        return textEntity;
    }

    public static void read(FilterContext filterContext, OutputPipe<TextEntity> outputPipe) {
        RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).get().forEach(outputPipe::forward);
    }

    public static void dropTable(FilterContext filterContext) {
        System.out.println(filterContext.properties());
        JDBCConnectionPool.getJDBCConnection(filterContext.properties(), filterContext)
                .tableCommand()
                .dropTable(TextEntity.class.getSimpleName().toLowerCase(Locale.ROOT))
                .asIgnore();

        System.out.println("Amount after drop table" + RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).get().size());

    }
}