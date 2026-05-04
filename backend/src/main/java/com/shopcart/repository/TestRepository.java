package com.shopcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.TestEntity;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long>{
    // 
}
