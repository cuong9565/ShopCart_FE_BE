package com.shopcart.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopcart.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository <PaymentEntity, UUID> {
     // Các phương thức truy vấn
     Optional<PaymentEntity> findByOrderId(UUID orderId);

}
