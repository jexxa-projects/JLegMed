package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public class StatefulFilter {

    public static TextEntity add(TextEntity textEntity, FilterContext filterContext) {
        System.err.println("> " + RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).repository().get().size());

        RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).add(textEntity);
        System.err.println("< " + RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).repository().get().size());
        return textEntity;
    }

    public static void read(FilterContext filterContext, OutputPipe<TextEntity> outputPipe) {
        RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).get().forEach(outputPipe::forward);
    }

    public static void clearTable(FilterContext filterContext) {
        RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).removeAll();
    }
}