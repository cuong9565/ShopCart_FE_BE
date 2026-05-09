// package com.shopcart.service;

// import com.shopcart.util.OrderRequest;
// import com.shopcart.util.OrderRequest.OrderItemReq;
// import com.shopcart.util.OrderResponse;

// import java.math.BigDecimal;
// import java.util.List;
// import java.util.UUID;

// /**
//  * Contract cho toàn bộ nghiệp vụ Order.
//  * Implementation: {@link OrderServiceImpl}
//  */
// public interface OrderService {

//     /**
//      * Tạo đơn hàng mới:
//      * validate → tính giá → trừ tồn kho → lưu DB → xóa giỏ hàng.
//      */
//     OrderResponse createOrder(UUID userId, OrderRequest req);

//     /** Lấy chi tiết một đơn hàng (chỉ chủ đơn mới xem được). */
//     OrderResponse getOrderById(UUID orderId, UUID userId);

//     /** Lấy toàn bộ đơn hàng của một user. */
//     List<OrderResponse> getOrdersByUser(UUID userId);

//     /** Lấy đơn hàng của user, lọc theo trạng thái. */
//     List<OrderResponse> getOrdersByUserAndStatus(UUID userId, String status);

//     /**
//      * Hủy đơn hàng (chỉ PENDING / CONFIRMED).
//      * Tự động hoàn lại tồn kho cho từng sản phẩm.
//      */
//     OrderResponse cancelOrder(UUID orderId, UUID userId);

//     /**
//      * Admin / Staff cập nhật trạng thái đơn hàng.
//      * Kiểm tra chuyển trạng thái hợp lệ trước khi lưu.
//      */
//     OrderResponse updateStatus(UUID orderId, String newStatus);

//     /**
//      * Tính tổng tiền đơn hàng (subtotal – discount + shippingFee).
//      * Dùng cho preview checkout hoặc unit test.
//      */
//     BigDecimal calculateOrderTotal(List<OrderItemReq> items,
//                                    String couponCode,
//                                    BigDecimal shippingFee);

//     /**
//      * Kiểm tra tất cả sản phẩm trong danh sách có đủ tồn kho không.
//      * Trả về {@code false} ngay khi gặp sản phẩm đầu tiên không đủ.
//      */
//     boolean checkStockBeforeOrder(List<OrderItemReq> items);
// }

package com.shopcart.service;

import com.shopcart.entity.OrderItemEntity;
import com.shopcart.dto.OrderResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Contract cho toàn bộ nghiệp vụ Order.
 * Implementation: {@link OrderServiceImpl}
 */
public interface OrderService {

    /**
     * Tạo đơn hàng mới:
     * Nhận các tham số trực tiếp thay vì qua OrderRequest object.
     */
    OrderResponse createOrder(UUID userId, 
                              UUID addressId, 
                              String couponCode, 
                              Integer paymentMethod, 
                              BigDecimal shippingFee, 
                              List<OrderItemEntity> itemsToCreate);

    /** Lấy chi tiết một đơn hàng (chỉ chủ đơn hoặc admin mới xem được). */
    OrderResponse getOrderById(UUID orderId, UUID userId);

    /** Lấy toàn bộ đơn hàng của một user. */
    List<OrderResponse> getOrdersByUser(UUID userId);

    /** Lấy đơn hàng của user, lọc theo trạng thái. */
    List<OrderResponse> getOrdersByUserAndStatus(UUID userId, String status);

    /**
     * Hủy đơn hàng (chỉ trạng thái PENDING / CONFIRMED mới được hủy).
     * Tự động hoàn lại tồn kho cho từng sản phẩm.
     */
    OrderResponse cancelOrder(UUID orderId, UUID userId);

    /**
     * Admin / Staff cập nhật trạng thái đơn hàng.
     * Kiểm tra tính hợp lệ của việc chuyển trạng thái (StateMachine).
     */
    OrderResponse updateStatus(UUID orderId, String newStatus);

    /**
     * Tính tổng tiền đơn hàng (subtotal – discount + shippingFee).
     * Sử dụng danh sách các Item thực thể để tính toán.
     */
    BigDecimal calculateOrderTotal(List<OrderItemEntity> items,
                                   String couponCode,
                                   BigDecimal shippingFee);

    /**
     * Kiểm tra tồn kho trước khi đặt hàng.
     */
    boolean checkStockBeforeOrder(List<OrderItemEntity> items);
}