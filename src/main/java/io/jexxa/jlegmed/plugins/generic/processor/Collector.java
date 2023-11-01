package io.jexxa.jlegmed.plugins.generic.processor;

import java.util.Collection;

public class Collector<T> {
    private final Collection<T> collection;

    private Collector(Collection<T> collection)
    {
        this.collection = collection;
    }

    protected T collect(T content) {
        collection.add(content);
        return content;
    }

    public static  <T> Collector<T> collector(Collection<T> collection)
    {
        return new Collector<>(collection);
    }
}
