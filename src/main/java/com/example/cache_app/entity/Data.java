package com.example.cache_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Data class representing the objects stored in the cache and database.
 */
@Entity
@Getter
@Setter
public class Data implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    private String data;
    public Data(){}
    public Data(long id, String data) {
        this.id = id;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}