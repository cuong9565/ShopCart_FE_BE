// package com.shopcart.controller;

// import com.shopcart.service.OrderService;
// // import com.shopcart.util.OrderRequest;
// import com.shopcart.dto.OrderResponse;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.UUID;

// @RestController
// @RequestMapping("/api/orders")
// @RequiredArgsConstructor
// public class OrderController {

//     private final OrderService orderService;

//     /**
//      * POST /api/orders?userId=…
//      * Tạo đơn hàng mới.
//      */
//     @PostMapping
//     public ResponseEntity<OrderResponse> createOrder(
//             @RequestParam UUID userId,
//             @Valid @RequestBody OrderRequest req) {
//         return ResponseEntity
//                 .status(HttpStatus.CREATED)
//                 .body(orderService.createOrder(userId, req));
//     }

//     /**
//      * GET /api/orders/{orderId}?userId=…
//      * Lấy chi tiết đơn hàng.
//      */
//     @GetMapping("/{orderId}")
//     public ResponseEntity<OrderResponse> getOrder(
//             @PathVariable UUID orderId,
//             @RequestParam UUID userId) {
//         return ResponseEntity.ok(orderService.getOrderById(orderId, userId));
//     }

//     /**
//      * GET /api/orders?userId=…&status=…
//      * Danh sách đơn hàng của user (có thể lọc theo status).
//      */
//     @GetMapping
//     public ResponseEntity<List<OrderResponse>> listOrders(
//             @RequestParam UUID userId,
//             @RequestParam(required = false) String status) {
//         List<OrderResponse> list = (status != null && !status.isBlank())
//                 ? orderService.getOrdersByUserAndStatus(userId, status)
//                 : orderService.getOrdersByUser(userId);
//         return ResponseEntity.ok(list);
//     }

//     /**
//      * PATCH /api/orders/{orderId}/cancel?userId=…
//      * Hủy đơn hàng.
//      */
//     @PatchMapping("/{orderId}/cancel")
//     public ResponseEntity<OrderResponse> cancelOrder(
//             @PathVariable UUID orderId,
//             @RequestParam UUID userId) {
//         return ResponseEntity.ok(orderService.cancelOrder(orderId, userId));
//     }

//     /**
//      * PATCH /api/orders/{orderId}/status?status=…
//      * Admin cập nhật trạng thái đơn hàng.
//      */
//     @PatchMapping("/{orderId}/status")
//     public ResponseEntity<OrderResponse> updateStatus(
//             @PathVariable UUID orderId,
//             @RequestParam String status) {
//         return ResponseEntity.ok(orderService.updateStatus(orderId, status));
//     }
// }
package com.shopcart.controller;

import com.shopcart.dto.OrderResponse;
import com.shopcart.entity.OrderItemEntity;
import com.shopcart.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Tạo đơn hàng mới
     * Client gửi JSON chứa các thông tin như: userId, addressId, items, couponCode, v.v.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody Map<String, Object> payload) {
        // Parse dữ liệu từ payload (Cách này linh hoạt khi bạn không muốn dùng DTO Request cố định)
        UUID userId = UUID.fromString(payload.get("userId").toString());
        UUID addressId = UUID.fromString(payload.get("addressId").toString());
        String couponCode = (String) payload.get("couponCode");
        Integer paymentMethod = (Integer) payload.get("paymentMethod");
        
        // Chuyển đổi shippingFee an toàn
        BigDecimal shippingFee = payload.get("shippingFee") != null 
            ? new BigDecimal(payload.get("shippingFee").toString()) 
            : BigDecimal.ZERO;

        // Lấy danh sách items (Giả định phía Client gửi đúng cấu trúc OrderItemEntity)
     
        List<OrderItemEntity> items = (List<OrderItemEntity>) payload.get("items");

        OrderResponse response = orderService.createOrder(
                userId, 
                addressId, 
                couponCode, 
                paymentMethod, 
                shippingFee, 
                items
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Lấy chi tiết đơn hàng
     * Cần userId để validate quyền sở hữu (Security context thường sẽ lấy từ Token)
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id, @RequestParam UUID userId) {
        return ResponseEntity.ok(orderService.getOrderById(id, userId));
    }


    /**
     * Hủy đơn hàng
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id, @RequestParam UUID userId) {
        return ResponseEntity.ok(orderService.cancelOrder(id, userId));
    }
}