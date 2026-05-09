// package com.shopcart.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;  
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import java.util.UUID;

// /**
//  * Entity cho bảng OrderCoupons
//  * Lưu thông tin mã giảm giá đã áp dụng cho đơn hàng
//  */

// @Entity
// @Table(name = "order_coupons")
// @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
// public class OrderCouponsEntity {

//     /** ID mã giảm giá */
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private UUID id;

//     /** ID đơn hàng mà mã giảm giá được áp dụng */
//     @Column(name = "order_id", nullable = false)
//     private UUID orderId;

//     /** ID mã giảm giá */
//     @Column(name = "coupon_id", nullable = false)
//     private UUID couponId;
// }


package com.shopcart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity cho bảng OrderCoupons
 * Lưu vết mối quan hệ giữa Đơn hàng và Mã giảm giá đã sử dụng
 */
@Entity
@Table(name = "order_coupons")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class OrderCouponsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** 
     * Quan hệ ManyToOne với Order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** 
     * Quan hệ ManyToOne với CouponsEntity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponsEntity coupon;

    /**
     * Số tiền thực tế đã giảm tại thời điểm áp dụng.
     * Lưu trữ để đối soát lịch sử chính xác.
     */
    @Column(name = "applied_amount", precision = 19, scale = 2)
    private BigDecimal appliedAmount;
}