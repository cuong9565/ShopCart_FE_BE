package com.shopcart.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    /** Tìm tồn kho theo ID sản phẩm */
    Inventory findByProductId(UUID productId);
}