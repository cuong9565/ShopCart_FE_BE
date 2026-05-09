package com.shopcart.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopcart.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Inventory findByProductId(UUID productId);
}
