package com.example.cache_app;

import com.example.cache_app.entity.Data;
import com.example.cache_app.service.CachingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CacheServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CachingService cachingService;

    @Test
    void testAddEntity() throws Exception {
        String json = "{\"id\":1,\"data\":\"Sample Data\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cache")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Entity added to cache."));
    }

    @Test
    void testRemoveEntity() throws Exception {
        cachingService.add(new Data() {{ setId(1L); setData("Sample Data"); }});

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cache/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Entity removed from cache and database."));
    }

    @Test
    void testClearCache() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cache/clear"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cache cleared."));
    }
}
