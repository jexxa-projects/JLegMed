package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public class StatefulFilter {

    public static TextEntity add(TextEntity textEntity, FilterContext filterContext) {
        RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).add(textEntity);
        return textEntity;
    }

    public static void read(FilterContext filterContext, OutputPipe<TextEntity> outputPipe) {
        RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).get().forEach(outputPipe::forward);
    }

    public static void clearTable(FilterContext filterContext) {
        RepositoryPool.getRepository(TextEntity.class, TextEntity::key, filterContext).removeAll();
    }
}