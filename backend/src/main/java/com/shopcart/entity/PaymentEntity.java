// package com.shopcart.entity;

// import java.time.LocalDateTime;
// import java.util.UUID;

// import org.hibernate.annotations.UuidGenerator;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Entity
// @Table(name = "payment")
// @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

// public class PaymentEntity {

//     @Id
//     @UuidGenerator
//     @Column(updatable = false, nullable = false)
//     private UUID id;
 
//     @OneToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "order_id", nullable = false, unique = true)
//     private Order order;
 
//     /**
//      * Phương thức thanh toán:
//      *  1 = COD
//      *  2 = BANK_TRANSFER
//      */
//     @Column(nullable = false)
//     private Integer method;
 
//     @Column(nullable = false)
//     @Builder.Default
//     private String status = "PENDING";
 
//     @Column(name = "paid_at")
//     private LocalDateTime paidAt;

// }


package com.shopcart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity cho bảng payment
 * Lưu trữ thông tin giao dịch thanh toán của đơn hàng
 */
@Entity
@Table(name = "payments")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    /**
     * Mỗi đơn hàng chỉ có một bản ghi thanh toán.
     * Sử dụng FetchType.LAZY để tối ưu hiệu năng.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    /**
     * Phương thức thanh toán: 1 = COD, 2 = BANK_TRANSFER
     */
    @Column(nullable = false)
    private Integer method;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    //============= CONSTANTS =============
    public static final int METHOD_COD = 1;
    public static final int METHOD_BANK_TRANSFER = 2;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    //============= Helper Methods =============

    /**
     * Cập nhật trạng thái thanh toán thành công
     */
    public void markAsPaid() {
        this.status = STATUS_COMPLETED;
        this.paidAt = LocalDateTime.now();
    }
}