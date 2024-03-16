package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.common.drivenadapter.persistence.repository.IRepository;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.jexxa.common.drivenadapter.persistence.RepositoryFactory.createRepository;


public class Repository<T, K> {

    private final IRepository<T, K> iRepository;
    private final Function<T, K> keyFunction;

    Repository(Class<T> aggregateClazz,
               Function<T,K> keyFunction,
               FilterContext filterContext)
    {
        this.iRepository = createRepository(aggregateClazz, keyFunction, filterContext.properties());
        this.keyFunction = keyFunction;
    }

    /**
     * Updates the given aggregate inside the repository.
     * @param aggregate that should be updated
     * @pre Given aggregate must be added by using {@link #add(Object)}}
     */
    public T update(T aggregate)
    {
        iRepository.update(aggregate);
        return aggregate;
    }

    /**
     * Removed aggregate identified by given key.
     * @param key to the aggregate to be removed
     * @pre Aggregate must be added by using {@link #add(Object)}}
     */
    public K remove(K key) {
        iRepository.remove(key);
        return key;
    }

    /**
     * Adds an aggregate to this repository
     * @param aggregate that should be added
     * @pre Aggregate must not be added before
     */
    public T add(T aggregate) {
        iRepository.add(aggregate);
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
        return iRepository.get(key);
    }

    public List<T> get()
    {

        return iRepository.get();
    }

}
