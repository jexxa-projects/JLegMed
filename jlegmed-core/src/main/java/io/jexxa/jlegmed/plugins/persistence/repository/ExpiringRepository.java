package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.common.drivenadapter.persistence.repository.IRepository;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import static io.jexxa.common.drivenadapter.persistence.RepositoryFactory.createRepository;

public class ExpiringRepository<T,K> extends Repository<T, K>{
    private final IRepository<ExpiringRecord, String> ttlRepository;

    ExpiringRepository(
            Class<T> aggregateClazz,
            Function<T, K> keyFunction,
            String storageName,
            FilterContext filterContext)
    {
        super(aggregateClazz, keyFunction, storageName, filterContext);
        String storageNameTTL = storageName + "_ttl";
        ttlRepository =  createRepository(ExpiringRecord.class,
                ExpiringRecord::key, storageNameTTL, filterContext.properties());

    }

    synchronized void expireIn(K key, Duration ttl){
        expireAt(key, Instant.now().plus(ttl));
    }

    synchronized void expireAt(K key, Instant timestamp){
        if (get(key).isEmpty()){
            throw new IllegalArgumentException("key is managed in this repository");
        }

        var keyIdent = KeyIdentifier.of(key);
        if (ttlRepository.get(keyIdent).isPresent()) {
            ttlRepository.update(new ExpiringRecord(KeyIdentifier.of(key), timestamp));
        } else {
            ttlRepository.add(new ExpiringRecord(KeyIdentifier.of(key), timestamp));
        }
    }

    synchronized public K remove(K key) {
        var keyIdent = KeyIdentifier.of(key);
        super.remove(key);
        if (ttlRepository.get(keyIdent).isPresent()) {
            ttlRepository.remove(keyIdent);
        }
        return key;
    }

    record ExpiringRecord(String key, Instant expireAt) {}
}
