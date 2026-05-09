package com.shopcart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopcart.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByIsFeatured(Boolean isFeatured);
}
