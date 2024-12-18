package com.example.cache_app.service;

import com.example.cache_app.entity.Data;

/**
 * Interface defining the caching service operations.
 */
public interface CachingService {
    void add(Data entity);
    void remove(Long id);
    void removeAll();
    Data get(Long id);
    void clear();
}