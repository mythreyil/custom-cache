package com.example.cache_app.controller;

import com.example.cache_app.entity.Data;
import com.example.cache_app.service.CachingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller exposing APIs for cache management.
 */
@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {

    @Autowired
    private CachingService cachingService;

    /**
     * Adds an entity to the cache.
     * @param entity the entity to add.
     * @return success message.
     */
    @PostMapping
    public String add(@RequestBody Data entity) {
        cachingService.add(entity);
        return "Entity added to cache.";
    }

    /**
     * Removes an entity by ID from cache and database.
     * @param id the ID of the entity to remove.
     * @return success message.
     */
    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        cachingService.remove(id);
        return "Entity removed from cache and database.";
    }

    /**
     * Removes all entities from cache and database.
     * @return success message.
     */
    @DeleteMapping
    public String removeAll() {
        cachingService.removeAll();
        return "All entities removed from cache and database.";
    }

    /**
     * Fetches an entity by ID from cache or database.
     * @param id the ID of the entity to fetch.
     * @return the entity object.
     */
    @GetMapping("/{id}")
    public Data get(@PathVariable Long id) {
        return cachingService.get(id);
    }

    /**
     * Fetches all entities from cache or database.
     * @return all entities.
     */
    @GetMapping()
    public Map<Long, Data> getAll() {
        return cachingService.getAll();
    }

    /**
     * Clears the internal cache.
     * @return success message.
     */
    @PostMapping("/clear")
    public String clear() {
        cachingService.clear();
        return "Cache cleared.";
    }
}