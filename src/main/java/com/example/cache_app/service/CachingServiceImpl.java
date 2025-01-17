package com.example.cache_app.service;

import com.example.cache_app.entity.Data;
import com.example.cache_app.exception.CacheNotFoundException;
import com.example.cache_app.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the caching service using LRU eviction policy.
 */
@Service
public class CachingServiceImpl implements CachingService {
    private static final Logger log = LoggerFactory.getLogger(CachingServiceImpl.class);

    @Autowired
    private DataRepository repository;

    @Value("${cache.max.size:10}")
    public int maxCacheSize;

    private final Map<Long, Data> cache = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public void add(Data entity) {
        log.info("Adding entity with ID: {} to cache", entity.getId());
        if (cache.containsKey(entity.getId())) {
            cache.put(entity.getId(), entity); // Update cache if the data already exists
        } else if (cache.size() >= maxCacheSize) {
            // Evict the oldest entry
            Map.Entry<Long, Data> eldestEntry = cache.entrySet().iterator().next();
            cache.remove(eldestEntry.getKey());
            repository.save(eldestEntry.getValue()); // Save the evicted entry
        }
        cache.put(entity.getId(), entity); // Add new entry to the cache
    }

    @Override
    public void remove(Long id) {
        log.info("Removing entity with ID: {} from cache and database", id);
        cache.remove(id);
        repository.deleteById(id);
    }

    @Override
    public void removeAll() {
        log.info("Removing all entities from cache and database");
        cache.clear();
        repository.deleteAll();
    }

    @Override
    public Data get(Long id) {
        log.info("Fetching entity with ID: {} from cache or database", id);
        return cache.computeIfAbsent(id, key -> repository.findById(key)
                .orElseThrow(() -> new CacheNotFoundException("Data not found for id: " + id)));
    }

    @Override
    public Map<Long, Data> getAll() {
        return cache;
    }

    @Override
    public void clear() {
        log.info("Clearing the cache");
        cache.clear();
    }
}