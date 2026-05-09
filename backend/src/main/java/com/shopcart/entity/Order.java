// // package com.shopcart.entity;

// // import jakarta.persistence.CascadeType;
// // import jakarta.persistence.Column;
// // import jakarta.persistence.Entity;
// // import jakarta.persistence.FetchType;
// // import jakarta.persistence.GeneratedValue;
// // import jakarta.persistence.GenerationType;
// // import jakarta.persistence.Id;
// // import jakarta.persistence.OneToMany;
// // import jakarta.persistence.OneToOne;
// // import jakarta.persistence.Table;
// // import lombok.AllArgsConstructor;
// // import lombok.Builder;
// // import lombok.Getter;
// // import lombok.NoArgsConstructor;
// // import lombok.Setter;
// // import java.math.BigDecimal;
// // import java.time.LocalDateTime;
// // import java.util.ArrayList;
// // import java.util.List;
// // import java.util.UUID;

// // /**
// //  * Entity cho bảng Orders
// //  * Lưu thông tin đơn hàng của người dùng
// //  */

// // @Entity
// // @Table(name = "orders")
// // @Getter @Setter @AllArgsConstructor @Builder
// // public class OrderEntity {

// //     /** ID đơn hàng */
// //     @Id
// //     @GeneratedValue(strategy = GenerationType.IDENTITY)
// //     private UUID id;

// //     /** ID người dùng đặt hàng */
// //     @Column(name = "user_id", nullable = false)
// //     private UUID userId;

// //     /** ID địa chỉ giao hàng */
// //     @Column(name = "address_id", nullable = false)
// //     private UUID addressId;

// //     /**
// //      * Tổng giá trị đơn hàng trước khi áp dụng giảm giá và phí vận chuyển
// //      */
// //     @Column(name = "subtotal", nullable = false)
// //     private BigDecimal subtotal;

// //     /**
// //      * Số tiền giảm giá được áp dụng cho đơn hàng
// //      * Mặc định là 0 nếu không có mã giảm giá nào được áp dụng
// //      */
// //     @Column(name = "discount", nullable = false)
// //     private BigDecimal discount;

// //     /**
// //      * Phí vận chuyển cho đơn hàng
// //      * Mặc định là 0 nếu đơn hàng đủ điều kiện miễn phí vận chuyển hoặc chưa có thông tin về phí vận chuyển
// //      */
// //     @Column(name = "shipping_fee", nullable = false)
// //     private BigDecimal shippingFee;

// //     /**
// //      * Giá trị cuối cùng của đơn hàng sau khi đã áp dụng giảm giá và phí vận chuyển
// //      * final_price = subtotal + shippingFee - discount
// //      */
// //     @Column(name = "final_price", nullable = false)
// //     private BigDecimal finalPrice;

// //     /**
// //      * Trạng thái của đơn hàng
// //      */
// //     @Column(name = "status", nullable = false, length = 50)
// //     private String status;

// //     /**
// //      * Thời gian tạo đơn hàng
// //      */
// //     @Column(name = "created_at", nullable = false)
// //     private LocalDateTime createdAt;

// //     //=============Relationships================

// //     /**
// //      *
// //      */

// //     @OneToMany(mappedBy = "order", 
// //                cascade = CascadeType.ALL, 
// //                orphanRemoval = true,
// //                fetch = FetchType.LAZY)
// //     @Builder.Default   
// //     private List<OrderItemEntity> items = new ArrayList<>();

// //     /** 
// //      * 
// //      * 
// //      */
// //     @OneToOne(mappedBy = "order", 
// //               cascade = CascadeType.ALL, 
// //               orphanRemoval = true, 
// //               fetch = FetchType.LAZY)
// //     @Builder.Default
// //     private List<OrderCouponsEntity> orderCoupons = new ArrayList<>();

// //     /**
// //      * 
// //      */

// //     @OneToOne(mappedBy = "order", 
// //               cascade = CascadeType.ALL, 
// //               orphanRemoval = true, 
// //               fetch = FetchType.LAZY)
// //     @Builder.Default
// //     private PaymentEntity payment;

// // }

// package com.shopcart.entity;

// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.UUID;

// @Entity
// @Table(name = "orders")
// @Getter @Setter
// public class Order {
//     @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     private UUID id;

//     @Column(name = "user_id")
//     private UUID userId;

//     @Column(name = "address_id")
//     private UUID addressId;

//     private BigDecimal subtotal;
//     private BigDecimal discount;
//     private BigDecimal shippingFee;
//     private BigDecimal finalPrice;

//     private String status = "PENDING";

//     @Column(name = "created_at")
//     private LocalDateTime createdAt = LocalDateTime.now();

//     @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//     private List<OrderItemEntity> items;

//     public Object getOrderCoupons() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getOrderCoupons'");
//     }
// }

package com.shopcart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "address_id", nullable = false)
    private UUID addressId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discount;

    @Column(name = "shipping_fee", nullable = false, precision = 19, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "final_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal finalPrice;

    @Column(nullable = false, length = 50)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Thiết lập giá trị mặc định khi persist (lưu lần đầu)
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.discount == null)
            this.discount = BigDecimal.ZERO;
        if (this.shippingFee == null)
            this.shippingFee = BigDecimal.ZERO;
    }

    // Một đơn hàng có nhiều item
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();

    // Nếu đơn hàng có thể áp dụng nhiều coupon
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderCouponsEntity> orderCoupons = new ArrayList<>();

    // Một đơn hàng thường chỉ có một bản ghi thanh toán
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentEntity payment;

    // Địa chỉ giao hàng của đơn hàng
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "address_id", nullable = false)
    // private Address address;

    // Helper methods để thêm item dễ dàng hơn
    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

}