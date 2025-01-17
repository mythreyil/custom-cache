package com.example.cache_app;

import com.example.cache_app.entity.Data;
import com.example.cache_app.exception.CacheNotFoundException;
import com.example.cache_app.repository.DataRepository;
import com.example.cache_app.service.CachingServiceImpl;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class CachingServiceImplTest {

    @InjectMocks
    private CachingServiceImpl cachingService;

    @Mock
    private DataRepository repository;

    private final int maxCacheSize = 3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initializes mocks and injects them
        cachingService.maxCacheSize = maxCacheSize; // Set the max cache size
    }

    @Test
    void testAddEntityToCacheAndDatabase() {
        Data data = new Data(1L, "Test Data");
        cachingService.add(data);

        assertEquals(data, cachingService.get(data.getId()));
    }

    @Test
    void testEvictionPolicyWhenCacheExceedsMaxSize() {
        Data data1 = new Data(1L, "Data 1");
        Data data2 = new Data(2L, "Data 2");
        Data data3 = new Data(3L, "Data 3");
        Data data4 = new Data(4L, "Data 4");

        cachingService.add(data1); // repository.save(data1)
        cachingService.add(data2); // repository.save(data2)
        cachingService.add(data3); // repository.save(data3)
        cachingService.add(data4); // repository.save(data4), evict data1 and save evicted data1

        // Assert eviction of data1
        assertThrows(CacheNotFoundException.class, () -> cachingService.get(data1.getId()));
        // Assert data4 is in cache
        assertEquals(data4, cachingService.get(data4.getId()));

        // Verify save calls
        verify(repository, times(1)).save(any(Data.class)); // 1 saves in total
    }


    @Test
    void testRemoveEntityFromCacheAndDatabase() {
        Data data = new Data(1L, "Test Data");
        cachingService.add(data);

        cachingService.remove(data.getId());
        assertThrows(CacheNotFoundException.class, () -> cachingService.get(data.getId()));
        verify(repository, times(1)).deleteById(data.getId());
    }

    @Test
    void testRemoveAllEntitiesFromCacheAndDatabase() {
        Data data1 = new Data(1L, "Data 1");
        Data data2 = new Data(2L, "Data 2");

        cachingService.add(data1);
        cachingService.add(data2);

        cachingService.removeAll();
        assertTrue(cachingService.getAll().isEmpty());
        verify(repository, times(1)).deleteAll();
    }

    @Test
    void testFetchEntityFromCache() {
        Data data = new Data(1L, "Test Data");
        cachingService.add(data);

        Data result = cachingService.get(data.getId());
        assertEquals(data, result);
        verify(repository, never()).findById(data.getId()); // Should not call the repository
    }

    @Test
    void testFetchEntityFromDatabaseIfNotInCache() {
        Long id = 1L;
        Data data = new Data(id, "Test Data");
        when(repository.findById(id)).thenReturn(Optional.of(data));

        Data result = cachingService.get(id);
        assertEquals(data, result);
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testThrowExceptionWhenEntityNotFound() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CacheNotFoundException.class, () -> cachingService.get(id));
    }

    @Test
    void testGetAllEntitiesInCache() {
        Data data1 = new Data(1L, "Data 1");
        Data data2 = new Data(2L, "Data 2");

        cachingService.add(data1);
        cachingService.add(data2);

        assertEquals(2, cachingService.getAll().size());
        assertTrue(cachingService.getAll().containsValue(data1));
        assertTrue(cachingService.getAll().containsValue(data2));
    }

    @Test
    void testClearCache() {
        Data data = new Data(1L, "Test Data");
        cachingService.add(data);

        cachingService.clear();
        assertTrue(cachingService.getAll().isEmpty());
    }

    @Test
    void testConstraintViolationException() {

        assertThrows(ConstraintViolationException.class, () -> cachingService.add(new Data(1L, null)));
    }
}
