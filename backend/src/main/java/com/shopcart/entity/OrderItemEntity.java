// // package com.shopcart.entity;

// // import java.math.BigDecimal;
// // import java.util.UUID;

// // import org.hibernate.annotations.UuidGenerator;

// // import jakarta.persistence.Column;
// // import jakarta.persistence.Entity;
// // import jakarta.persistence.FetchType;
// // import jakarta.persistence.Id;
// // import jakarta.persistence.JoinColumn;
// // import jakarta.persistence.ManyToOne;
// // import jakarta.persistence.Table;
// // import lombok.AllArgsConstructor;
// // import lombok.Builder;
// // import lombok.Getter;
// // import lombok.NoArgsConstructor;
// // import lombok.Setter;

// // @Entity
// // @Table(name = "order_item")
// // @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

// // public class OrderItemEntity {
    
// //     /** ID chi tiết đơn hàng */
// //     @Id
// //     @UuidGenerator
// //     @Column(updatable = false, nullable = false)
// //     private UUID id;

// //     /** Đơn hàng tương ứng  */
// //     @ManyToOne(fetch = FetchType.LAZY)
// //     @JoinColumn(name = "order_id", nullable = false)
// //     private OrderEntity order;

// //     /** Sản phẩm tương ứng  */
// //     @ManyToOne(fetch = FetchType.LAZY)
// //     @JoinColumn(name = "product_id", nullable = false)
// //     private Product product;

// //     /** Số lượng sản phẩm */
// //     @Column(nullable = false)
// //     private Integer quantity;
    
// //     /** Giá tại thời điểm mua (snapshot – không bị ảnh hưởng khi giá sản phẩm thay đổi) */
// //     @Column(nullable = false, precision = 12, scale = 2)
// //     private BigDecimal price;

// // }


// package com.shopcart.entity;

// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;
// import java.math.BigDecimal;
// import java.util.UUID;

// @Entity
// @Table(name = "order_item")
// @Getter @Setter
// public class OrderItemEntity {
//     @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     private UUID id;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "order_id")
//     private Order order;

//     @Column(name = "product_id")
//     private UUID productId;

//     private Integer quantity;
//     private BigDecimal price; // Đơn giá tại thời điểm mua
// }

package com.shopcart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity cho bảng order_item
 * Lưu chi tiết từng sản phẩm trong một đơn hàng
 */
@Entity
@Table(name = "order_items") // Nên dùng số nhiều cho tên bảng để đồng nhất
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** 
     * Quan hệ ManyToOne với Order
     * Dùng @JoinColumn để chỉ định khóa ngoại
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** 
     * ID của sản phẩm. 
     * Lưu ý: Nếu bạn có ProductEntity, nên map @ManyToOne tương tự như Order.
     * Nếu không, lưu UUID như bên dưới là ổn.
     */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

    /** 
     * Giá snapshot tại thời điểm mua.
     * Dùng precision/scale để đảm bảo độ chính xác tiền tệ.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    //============= Helper Methods =============

    /**
     * Tính thành tiền của item này (giá * số lượng)
     */
    public BigDecimal getSubtotal() {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}

