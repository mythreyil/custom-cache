package com.example.cache_app.service;

import com.example.cache_app.entity.Data;
import com.example.cache_app.exception.CacheNotFoundException;
import com.example.cache_app.repository.DataRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

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

    public final ReentrantLock lock = new ReentrantLock();

    private final Map<Long, Data> cache = new LinkedHashMap<>();

    @Override
    public void add(Data entity) {
        lock.lock();
        try {
            if(entity.getId() == null || entity.getData() == null || entity.getData().isBlank()) {
                throw new ConstraintViolationException("ID & Data can not be null", null,null);
            }
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
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(Long id) {
        lock.lock();
        try {
            log.info("Removing entity with ID: {} from cache and database", id);
            cache.remove(id);
            repository.deleteById(id);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeAll() {
        lock.lock();
        try {
            log.info("Removing all entities from cache and database");
            cache.clear();
            repository.deleteAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Data get(Long id) {
        lock.lock();
        try {
            log.info("Fetching entity with ID: {} from cache or database", id);
            return cache.computeIfAbsent(id, key -> repository.findById(key)
                    .orElseThrow(() -> new CacheNotFoundException("Data not found for id: " + id)));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<Long, Data> getAll() {
        lock.lock();
        try {
            return cache;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            log.info("Clearing the cache");
            cache.clear();
        } finally {
            lock.unlock();
        }
    }
}