package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.common.drivenadapter.persistence.repository.IRepository;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.jexxa.common.drivenadapter.persistence.RepositoryManager.getRepository;


public class RepositoryProcessor<T, K> {

    private final IRepository<T, K> repository;
    Function<T, K> keyFunction;

    RepositoryProcessor(Class<T> aggregateClazz,
                        Function<T,K> keyFunction,
                        FilterContext filterContext)
    {
        this.repository = getRepository(aggregateClazz, keyFunction, filterContext.properties());
        this.keyFunction = keyFunction;
    }

    /**
     * Updates the given aggregate inside the repository.
     * @param aggregate that should be updated
     * @pre Given aggregate must be added by using {@link #add(Object)}}
     */
    public T update(T aggregate)
    {
        repository.update(aggregate);
        return aggregate;
    }

    /**
     * Removed aggregate identified by given key.
     * @param key to the aggregate to be removed
     * @pre Aggregate must be added by using {@link #add(Object)}}
     */
    public K remove(K key) {
        repository.remove(key);
        return key;
    }

    /**
     * Adds an aggregate to this repository
     * @param aggregate that should be added
     * @pre Aggregate must not be added before
     */
    public T add(T aggregate) {
        repository.add(aggregate);
        return aggregate;
    }

    /**
     * Either updates an aggregate if exists, or adds an aggregate to this repository
     * @param aggregate that should be added or updated
     */
    public T put(T aggregate) {
        if ( get(keyFunction.apply(aggregate)).isPresent()) {
            return update(aggregate);
        }

        return add(aggregate);
    }

    /**
     * Returns the aggregate identified by given key.
     * @param key that identifies the aggregate
     * @return Optional of aggregate. Optional is empty if given key finds no aggregate.
     */
    public Optional<T> get(K key)
    {
        return repository.get(key);
    }

    public List<T> get()
    {

        return repository.get();
    }

}
