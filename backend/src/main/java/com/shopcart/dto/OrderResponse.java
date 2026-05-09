// package com.shopcart.dto;

// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.UUID;

// /** DTO trả về thông tin đơn hàng */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class OrderResponse {

//         private UUID id;
//         private UUID userId;
//         private UUID addressId;
//         private List<ItemResp> items;
//         private BigDecimal totalPrice;
//         private BigDecimal discount;
//         private BigDecimal shippingFee;
//         private BigDecimal finalPrice;
//         private String status;
//         private String couponCode;
//         private LocalDateTime createdAt;
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
//         public static class ItemResp {
//                 private UUID id;
//                 private UUID productId;
//                 private String productName;
//                 private BigDecimal unitPrice;
//                 private Integer quantity;
//                 private BigDecimal lineTotal;
//         }
// }


package com.shopcart.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO trả về thông tin chi tiết đơn hàng cho Client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private UUID id;
    private UUID userId;
    private UUID addressId;
    
    // Danh sách các mặt hàng trong đơn
    private List<ItemResp> items;
    
    private BigDecimal subtotal;    // Tổng tiền hàng (trước giảm giá)
    private BigDecimal discount;    // Số tiền được giảm
    private BigDecimal shippingFee; // Phí vận chuyển
    private BigDecimal finalPrice;  // Tổng thanh toán cuối cùng
    
    private String status;
    private String couponCode;      // Mã coupon đã áp dụng (nếu có)
    
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;    // Thêm thông tin thời gian thanh toán từ PaymentEntity

    /**
     * DTO chi tiết từng item
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemResp {
        private UUID id;
        private UUID productId;
        private String productName;  // Lấy từ ProductEntity khi mapping
        private BigDecimal unitPrice; // Giá tại thời điểm mua
        private Integer quantity;
        private BigDecimal lineTotal; // unitPrice * quantity
    }
}