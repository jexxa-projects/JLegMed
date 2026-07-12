package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.common.drivenadapter.persistence.repository.IRepository;
import io.jexxa.common.facade.json.JSONManager;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static io.jexxa.common.drivenadapter.persistence.RepositoryFactory.createRepository;

public class ExpiringRepository<T,K> extends Repository<T, K>{
    private final IRepository<ExpiringRecord, String> ttlRepository;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private long purgeInterval = 60;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private final Class<K> keyClazz;
    ExpiringRepository(
            Class<T> aggregateClazz,
            Class<K> keyClazz,
            Function<T, K> keyFunction,
            String storageName,
            FilterContext filterContext)
    {
        super(aggregateClazz, keyFunction, storageName, filterContext);
        String storageNameTTL = storageName + "_ttl";
        ttlRepository =  createRepository(ExpiringRecord.class,
                ExpiringRecord::key, storageNameTTL, filterContext.properties());
        this.keyClazz = keyClazz;
        this.scheduler.scheduleAtFixedRate(this::purgeData, purgeInterval, purgeInterval, timeUnit);
    }

    public void purgeInterval(long interval, TimeUnit timeUnit ) {
        shutdown();
        this.purgeInterval = interval;
        this.timeUnit = timeUnit;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(this::purgeData, purgeInterval, purgeInterval, timeUnit);
    }

    public synchronized void expireIn(K key, Duration ttl){
        expireAt(key, Instant.now().plus(ttl));
    }

    public synchronized void expireAt(K key, Instant timestamp){
        if (get(key).isEmpty()){
            throw new IllegalArgumentException("key is not managed in this repository");
        }

        var keyIdent = JSONManager.getJSONConverter().toJson(key);
        if (ttlRepository.get(keyIdent).isPresent()) {
            ttlRepository.update(new ExpiringRecord(keyIdent, timestamp));
        } else {
            ttlRepository.add(new ExpiringRecord(keyIdent, timestamp));
        }
    }

    @Override
    public synchronized K remove(K key) {
        var keyIdent = JSONManager.getJSONConverter().toJson(key);
        super.remove(key);
        if (ttlRepository.get(keyIdent).isPresent()) {
            ttlRepository.remove(keyIdent);
        }
        return key;
    }

    public synchronized void purgeData() {
        var expiredData = ttlRepository
                .get()
                .stream()
                .filter(element -> element.expireAt().isBefore(Instant.now()))
                .map(data -> JSONManager.getJSONConverter().fromJson(data.key(), keyClazz))
                .toList();

        if (expiredData.isEmpty()) {
            SLF4jLogger.getLogger(ExpiringRepository.class).debug("No data expired" );
        } else {
            SLF4jLogger.getLogger(ExpiringRepository.class).info("Remove {} entries from Repository", expiredData.size() );
        }

        expiredData.forEach(this::remove);
    }

    private void shutdown() {
        scheduler.shutdown(); // Verhindert neue Tasks
        try {
            // Warte bis zu 5 Sekunden, ob der aktuelle Purge-Vorgang noch fertig wird
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // Erzwinge Abbruch, wenn es zu lange dauert
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt(); // Status wiederherstellen
        }
    }

    record ExpiringRecord(String key, Instant expireAt) {}
}
