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
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.UUID;

// /**
//  * Entity cho bảng Coupons
//  * Lưu thông tin mã giảm giá
//  */
// @Entity
// @Table(name = "coupons")
// @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
// public class CouponsEntity {

//     /** ID mã giảm giá */
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private UUID id;

//     /** Mã giảm giá (ví dụ: "DISCOUNT10") */
//     @Column(name = "code", nullable = false, unique = true)
//     private String code;

//     /** Giá trị giảm giá (%) */
//     @Column(name = "discount_value", nullable = false)
//     private BigDecimal discountValue;

//     /** 
//      * Loại giảm giá 
//      * 1 = "PERCENTAGE"  (%)cho giảm giá theo phần trăm, 
//      * 2 "FIXED" (VNĐ) cho giảm giá cố định (ví dụ: giảm 10.000VNĐ)
//     */
//     @Column(name = "discount_type", nullable = false)
//     private Integer discountType;

//     /** Ngày hết hạn của mã giảm giá */
//     @Column(name = "expiry_date", nullable = false)
//     private LocalDateTime expiryDate;

//     /** Giá trị đơn hàng tối thiểu để áp dụng mã giảm giá */
//     @Column(name = "min_order_value", precision = 10, scale = 2)
//     private BigDecimal minOrderValue;

//     /** Giá trị đơn hàng tối đa để áp dụng mã giảm giá */
//     @Column(name = "max_discount", precision = 10, scale = 2)
//     private BigDecimal maxDiscount;

//     //=============Helper Methods=============

//     /** 
//      * Kiểm tra xem loại giảm giá có phải là phần trăm hay không 
//      * @return true nếu là giảm giá theo phần trăm, false nếu là giảm giá cố định hoặc discountType null
//     */
//     public boolean isPercentage() {
//         return discountType != null && discountType == 1;
//     }
  
//     /** 
//      * Kiểm tra xem loại giảm giá có phải là cố định hay không 
//      * @return true nếu là giảm giá cố định, false nếu là giảm giá theo phần trăm hoặc discountType null
//     */
//     public boolean isFixed() {
//         return discountType != null && discountType == 2;
//     }
    
//     /**
//      * Kiểm tra xem mã giảm giá đã hết hạn hay chưa
//       * @return true nếu mã giảm giá đã hết hạn, false nếu còn hiệu lực hoặc expiryDate null
//      */
//     public boolean isExpired() {
//         return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
//     }


//     /**
//      * Kiểm tra xem mã giảm giá có hợp lệ để áp dụng cho đơn hàng hay không
//      * Điều kiện:
//      * - Không được hết hạn
//      * - Đơn hàng phải đạt giá trị tối thiểu nếu minOrderValue được thiết lập
//      * 
//      * @param orderValue Giá trị đơn hàng trước khi áp dụng giảm giá
//      * @return true nếu mã giảm giá hợp lệ, false nếu đã hết hạn hoặc không đạt giá trị đơn hàng tối thiểu
//      */
//     public boolean isValid(BigDecimal orderValue) {
//         if (isExpired()) {
//             return false;
//         }
//         if (minOrderValue != null && orderValue.compareTo(minOrderValue) < 0) {
//             return false;
//         }
//         return true;
//     }

//     /**
//      * Tính số tiền giảm thực té cho một đơn hàng
//      * @param subtotal Tổng giá trị đơn hàng trước khi áp dụng giảm giá
//      * @return số tiền được giảm (không âm, không vượt quá maxDiscount nếu là phần trăm, không vượt quá subtotal nếu là cố định)
//      */
//     public BigDecimal calculateDiscount(BigDecimal subtotal) {
//         if (isPercentage()) {
//             BigDecimal raw = subtotal.multiply(discountValue).divide(BigDecimal.valueOf(100));
//             if (maxDiscount != null && raw.compareTo(maxDiscount) > 0) {
//                 return maxDiscount;
//             }
//         return raw;
//     }
//     // Fixed: không trả về số âm
//     return discountValue.min(subtotal);
// }

// }

package com.shopcart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity cho bảng Coupons
 * Lưu thông tin mã giảm giá
 */
@Entity
@Table(name = "coupons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CouponsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // UUID nên dùng AUTO hoặc CUSTOM generator
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "discount_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountValue;

    /** 
     * 1 = "PERCENTAGE" (%), 2 = "FIXED" (VNĐ)
     */
    @Column(name = "discount_type", nullable = false)
    private Integer discountType;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "min_order_value", precision = 19, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "max_discount", precision = 19, scale = 2)
    private BigDecimal maxDiscount;

    //============= CONSTANTS =============
    public static final int TYPE_PERCENTAGE = 1;
    public static final int TYPE_FIXED = 2;

    //============= Helper Methods =============

    public boolean isPercentage() {
        return discountType != null && discountType == TYPE_PERCENTAGE;
    }
  
    public boolean isFixed() {
        return discountType != null && discountType == TYPE_FIXED;
    }
    
    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isValid(BigDecimal orderValue) {
        if (isExpired()) return false;
        
        // Nếu giá trị đơn hàng nhỏ hơn mức tối thiểu yêu cầu
        if (minOrderValue != null && orderValue.compareTo(minOrderValue) < 0) {
            return false;
        }
        return true;
    }

    /**
     * Tính số tiền giảm thực tế
     */
    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal calculatedDiscount = BigDecimal.ZERO;

        if (isPercentage()) {
            // Công thức: (subtotal * discountValue) / 100
            calculatedDiscount = subtotal.multiply(discountValue)
                                         .divide(BigDecimal.valueOf(100));
            
            // Giới hạn mức giảm tối đa nếu có cấu hình maxDiscount
            if (maxDiscount != null && calculatedDiscount.compareTo(maxDiscount) > 0) {
                calculatedDiscount = maxDiscount;
            }
        } else if (isFixed()) {
            calculatedDiscount = discountValue;
        }

        // Đảm bảo số tiền giảm không vượt quá tổng giá trị đơn hàng
        return calculatedDiscount.min(subtotal);
    }
}