package com.shopcart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>{
       // Các phương thức truy vấn
       List<Order> findByUserId(UUID userId);
       List<Order> findByIdAndStatus(UUID userId, String status);
       List<Order> findByStatus(String status);

    
}
