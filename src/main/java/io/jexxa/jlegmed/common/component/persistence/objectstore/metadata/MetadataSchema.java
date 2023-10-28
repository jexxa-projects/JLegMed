package io.jexxa.jlegmed.common.component.persistence.objectstore.metadata;


public interface MetadataSchema
{
    <T, S, V> MetaTag<T, S, V> getTag();
}