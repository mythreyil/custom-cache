package com.example.cache_app.repository;

import com.example.cache_app.entity.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repository interface for CRUD operations on Data.
 */
@Repository
public interface DataRepository extends JpaRepository<Data, Long> {}