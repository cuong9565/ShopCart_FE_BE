package com.shopcart.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.CouponsEntity;

@Repository
public interface CouponRepository extends JpaRepository<CouponsEntity, UUID> {
    // Các phương thức truy vấn
    Optional<CouponsEntity> findByCode(String code);
}