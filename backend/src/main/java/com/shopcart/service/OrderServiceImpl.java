// package com.shopcart.service;

// import com.shopcart.entity.*;
// import com.shopcart.repository.*;
// import com.shopcart.dto.OrderResponse;
// import com.shopcart.dto.OrderResponse.ItemResp;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;
// import java.util.stream.Collectors;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class OrderServiceImpl implements OrderService {

//         private final OrderRepository orderRepository;
//         private final ProductRepository productRepository;
//         private final UserRepository userRepository;
//         private final AddressRepository addressRepository;
//         private final CouponRepository couponRepository;
//         private final InventoryRepository inventoryRepository;
//         private final PaymentRepository paymentRepository;
//         // private final AddressRepository addressRepository;

//         // =========================================================================
//         // CREATE ORDER
//         // =========================================================================

//         @Override
//         @Transactional
//         public OrderResponse createOrder(UUID userId, UUID addressId, String couponCode,
//                         Integer paymentMethod, BigDecimal shippingFee,
//                         List<OrderItemEntity> itemsToCreate) {

//                 // 1. Validate User & Address (Giả định logic validate address thuộc user)
//                 userRepository.findById(userId)
//                                 .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

//                 // 2. Xử lý danh sách sản phẩm và tính Subtotal
//                 BigDecimal subtotal = BigDecimal.ZERO;
//                 List<OrderItemEntity> validatedItems = new ArrayList<>();

//                 for (OrderItemEntity item : itemsToCreate) {
//                         // 1. Tìm sản phẩm (ProductRepository thường trả về Optional mặc định của JPA)
//                         Product product = productRepository.findById(item.getProductId())
//                                         .orElseThrow(() -> new RuntimeException(
//                                                         "Sản phẩm không tồn tại: " + item.getProductId()));

//                         // 2. Kiểm tra trạng thái sản phẩm
//                         if (!"ACTIVE".equals(product.getStatus())) {
//                                 throw new RuntimeException("Sản phẩm không hoạt động: " + product.getName());
//                         }

//                         // 3. Tìm tồn kho - Trả về Inventory trực tiếp (không dùng Optional)
//                         Inventory inventory = inventoryRepository.findByProductId(product.getId());

//                         // Kiểm tra null bằng if truyền thống
//                         if (inventory == null) {
//                                 throw new RuntimeException(
//                                                 "Không tìm thấy thông tin tồn kho cho sản phẩm: " + product.getName());
//                         }

//                         // 4. Kiểm tra số lượng tồn kho
//                         if (inventory.getQuantity() < item.getQuantity()) {
//                                 throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ hàng. " +
//                                                 "Trong kho còn: " + inventory.getQuantity() + ", bạn cần: "
//                                                 + item.getQuantity());
//                         }

//                         // 5. Tính toán tiền bạc
//                         // Chú ý: Dùng snapshot giá từ bảng Product tại thời điểm này
//                         BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
//                         subtotal = subtotal.add(lineTotal);

//                         // 6. Cập nhật thông tin cho Item trước khi lưu
//                         item.setPrice(product.getPrice()); // Lưu giá snapshot để đối chiếu sau này
//                         validatedItems.add(item);
//                 }

//                 // 3. Áp dụng Coupon
//                 BigDecimal discount = BigDecimal.ZERO;
//                 CouponsEntity coupon = null;
//                 if (couponCode != null && !couponCode.isBlank()) {
//                         coupon = couponRepository.findByCode(couponCode)
//                                         .orElseThrow(() -> new RuntimeException("Mã giảm giá không hợp lệ"));

//                         if (!coupon.isValid(subtotal)) {
//                                 throw new RuntimeException("Mã giảm giá không đủ điều kiện áp dụng");
//                         }
//                         discount = coupon.calculateDiscount(subtotal);
//                 }

//                 // 4. Tính toán giá cuối
//                 BigDecimal finalPrice = subtotal.subtract(discount)
//                                 .add(shippingFee != null ? shippingFee : BigDecimal.ZERO);

//                 // 5. Trừ tồn kho (Atomic update)
//                 for (OrderItemEntity item : validatedItems) {
//                         int updated = inventoryRepository.decreaseStock(item.getProductId(), item.getQuantity());
//                         if (updated == 0) {
//                                 throw new RuntimeException("Lỗi cập nhật tồn kho cho sản phẩm: " + item.getProductId());
//                         }
//                 }

//                 // Bước lấy địa chỉ (trong hàm createOrder)
//                 Address address = addressRepository.findById(addressId)
//                                 .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));

//                 // 6. Lưu Đơn hàng
//                 Order order = new Order();
//                 order.setUserId(userId);
//                 order.setAddressId(address.getId());
//                 order.setSubtotal(subtotal);
//                 order.setDiscount(discount);
//                 order.setShippingFee(shippingFee);
//                 order.setFinalPrice(finalPrice);
//                 order.setStatus("PENDING");

//                 // Gắn items vào order
//                 for (OrderItemEntity item : validatedItems) {
//                         item.setOrder(order);
//                 }
//                 order.setItems(validatedItems);

//                 Order savedOrder = orderRepository.save(order);

//                 // 7. Lưu Coupon đã dùng (nếu có)
//                 if (coupon != null) {
//                         OrderCouponsEntity oc = OrderCouponsEntity.builder()
//                                         .order(savedOrder)
//                                         .coupon(coupon)
//                                         .appliedAmount(discount)
//                                         .build();
//                         // Lưu qua repository hoặc cascading nếu đã cấu hình
//                 }

//                 // 8. Tạo Payment
//                 PaymentEntity payment = PaymentEntity.builder()
//                                 .order(savedOrder)
//                                 .method(paymentMethod)
//                                 .status("PENDING")
//                                 .build();
//                 paymentRepository.save(payment);

//                 return mapToResponse(savedOrder, couponCode);
//         }

//         // =========================================================================
//         // MAPPING LOGIC (Thay thế cho OrderResponse.from)
//         // =========================================================================

//         private OrderResponse mapToResponse(Order order, String couponCode) {
//                 List<ItemResp> itemResps = order.getItems().stream().map(item -> {
//                         ItemResp resp = new ItemResp();
//                         resp.setId(item.getId());
//                         resp.setProductId(item.getProductId());
//                         resp.setQuantity(item.getQuantity());
//                         resp.setUnitPrice(item.getPrice());
//                         resp.setLineTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
//                         // Lưu ý: productName cần lấy từ ProductRepository nếu cần hiển thị tên
//                         return resp;
//                 }).collect(Collectors.toList());

//                 OrderResponse response = new OrderResponse();
//                 response.setId(order.getId());
//                 response.setUserId(order.getUserId());
//                 response.setAddressId(order.getAddressId());
//                 response.setItems(itemResps);
//                 response.setSubtotal(order.getSubtotal());
//                 response.setDiscount(order.getDiscount());
//                 response.setShippingFee(order.getShippingFee());
//                 response.setFinalPrice(order.getFinalPrice());
//                 response.setStatus(order.getStatus());
//                 response.setCouponCode(couponCode);
//                 response.setCreatedAt(order.getCreatedAt());

//                 return response;
//         }

//         @Override
//         public OrderResponse getOrderById(UUID orderId, UUID userId) {
//                 // TODO Auto-generated method stub
//                 throw new UnsupportedOperationException("Unimplemented method 'getOrderById'");
//         }

//         @Override
//         public List<OrderResponse> getOrdersByUser(UUID userId) {
//                 // TODO Auto-generated method stub
//                 throw new UnsupportedOperationException("Unimplemented method 'getOrdersByUser'");
//         }

//         @Override
//         public List<OrderResponse> getOrdersByUserAndStatus(UUID userId, String status) {
//                 // TODO Auto-generated method stub
//                 throw new UnsupportedOperationException("Unimplemented method 'getOrdersByUserAndStatus'");
//         }

//         @Override
//         public OrderResponse cancelOrder(UUID orderId, UUID userId) {
//                 // TODO Auto-generated method stub
//                 throw new UnsupportedOperationException("Unimplemented method 'cancelOrder'");
//         }

//         @Override
//         public OrderResponse updateStatus(UUID orderId, String newStatus) {
//                 // TODO Auto-generated method stub
//                 throw new UnsupportedOperationException("Unimplemented method 'updateStatus'");
//         }

//         @Override
//         public BigDecimal calculateOrderTotal(List<OrderItemEntity> items, String couponCode, BigDecimal shippingFee) {
//                 // TODO Auto-generated method stub
//                 throw new UnsupportedOperationException("Unimplemented method 'calculateOrderTotal'");
//         }

//         @Override
//         public boolean checkStockBeforeOrder(List<OrderItemEntity> items) {
//                 // TODO Auto-generated method stub
//                 throw new UnsupportedOperationException("Unimplemented method 'checkStockBeforeOrder'");
//         }
// }


package com.shopcart.service;

import com.shopcart.entity.*;
import com.shopcart.repository.*;
import com.shopcart.dto.OrderResponse;
import com.shopcart.dto.OrderResponse.ItemResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CouponRepository couponRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, UUID addressId, String couponCode,
                                    Integer paymentMethod, BigDecimal shippingFee,
                                    List<OrderItemEntity> itemsToCreate) {

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Sử dụng một List tạm để chứa các item đã validate
        List<OrderItemEntity> validatedItems = new ArrayList<>();

        for (OrderItemEntity item : itemsToCreate) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + item.getProductId()));

            if (!"ACTIVE".equals(product.getStatus())) {
                throw new RuntimeException("Sản phẩm không hoạt động: " + product.getName());
            }

            Inventory inventory = inventoryRepository.findByProductId(product.getId());
            if (inventory == null || inventory.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ hàng.");
            }

            item.setPrice(product.getPrice());
            validatedItems.add(item);
        }

        // Tính Subtotal an toàn bằng Stream reduce
        BigDecimal subtotal = validatedItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank()) {
            CouponsEntity coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không hợp lệ"));

            if (!coupon.isValid(subtotal)) {
                throw new RuntimeException("Mã giảm giá không đủ điều kiện áp dụng");
            }
            discount = coupon.calculateDiscount(subtotal);
        }

        BigDecimal finalPrice = subtotal.subtract(discount)
                .add(shippingFee != null ? shippingFee : BigDecimal.ZERO);

        // Trừ tồn kho (Atomic update)
        for (OrderItemEntity item : validatedItems) {
            int updated = inventoryRepository.decreaseStock(item.getProductId(), item.getQuantity());
            if (updated == 0) {
                throw new RuntimeException("Lỗi cập nhật tồn kho cho sản phẩm: " + item.getProductId());
            }
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));

        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(address.getId());
        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setShippingFee(shippingFee);
        order.setFinalPrice(finalPrice);
        order.setStatus("PENDING");

        for (OrderItemEntity item : validatedItems) {
            item.setOrder(order);
        }
        order.setItems(validatedItems);

        Order savedOrder = orderRepository.save(order);

        paymentRepository.save(PaymentEntity.builder()
                .order(savedOrder)
                .method(paymentMethod)
                .status("PENDING")
                .build());

        return mapToResponse(savedOrder, couponCode);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xem đơn hàng này");
        }
        return mapToResponse(order, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(UUID userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(order -> mapToResponse(order, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserAndStatus(UUID userId, String status) {
        return orderRepository.findByIdAndStatus(userId, status.toUpperCase()).stream()
                .map(order -> mapToResponse(order, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng ở trạng thái PENDING");
        }

        order.setStatus("CANCELLED");
        
        // Hoàn lại tồn kho
        for (OrderItemEntity item : order.getItems()) {
            inventoryRepository.increaseStock(item.getProductId(), item.getQuantity());
        }

        return mapToResponse(orderRepository.save(order), null);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(UUID orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        order.setStatus(newStatus.toUpperCase());
        return mapToResponse(orderRepository.save(order), null);
    }

    @Override
    public BigDecimal calculateOrderTotal(List<OrderItemEntity> items, String couponCode, BigDecimal shippingFee) {
        BigDecimal subtotal = items.stream()
                .map(item -> {
                    Product p = productRepository.findById(item.getProductId()).orElseThrow();
                    return p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null) {
            discount = couponRepository.findByCode(couponCode)
                    .map(c -> c.calculateDiscount(subtotal)).orElse(BigDecimal.ZERO);
        }
        return subtotal.subtract(discount).add(shippingFee != null ? shippingFee : BigDecimal.ZERO);
    }

    @Override
    public boolean checkStockBeforeOrder(List<OrderItemEntity> items) {
        for (OrderItemEntity item : items) {
            Inventory inv = inventoryRepository.findByProductId(item.getProductId());
            if (inv == null || inv.getQuantity() < item.getQuantity()) return false;
        }
        return true;
    }

    private OrderResponse mapToResponse(Order order, String couponCode) {
        List<ItemResp> itemResps = order.getItems().stream().map(item -> {
            ItemResp resp = new ItemResp();
            resp.setId(item.getId());
            resp.setProductId(item.getProductId());
            resp.setQuantity(item.getQuantity());
            resp.setUnitPrice(item.getPrice());
            // Tính lineTotal cục bộ để tránh lỗi final variable
            resp.setLineTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return resp;
        }).collect(Collectors.toList());

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setAddressId(order.getAddressId());
        response.setItems(itemResps);
        response.setSubtotal(order.getSubtotal());
        response.setDiscount(order.getDiscount());
        response.setShippingFee(order.getShippingFee());
        response.setFinalPrice(order.getFinalPrice());
        response.setStatus(order.getStatus());
        response.setCouponCode(couponCode);
        response.setCreatedAt(order.getCreatedAt());

        return response;
    }
}