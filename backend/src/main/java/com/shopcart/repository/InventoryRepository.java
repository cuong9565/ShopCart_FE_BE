package com.shopcart.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    /** Tìm tồn kho theo ID sản phẩm */
    Inventory findByProductId(UUID productId);

    /** 
     * Trừ tồn kho một cách an toàn (Atomic Update)
     * @return số dòng bị ảnh hưởng (1 nếu thành công, 0 nếu thất bại/hết hàng)
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity - :qty " +
           "WHERE i.productId = :pid AND i.quantity >= :qty")
    int decreaseStock(@Param("pid") UUID productId, @Param("qty") Integer quantity);
    
    /** 
     * Hoàn lại tồn kho khi hủy đơn hàng 
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity + :qty " +
           "WHERE i.productId = :pid")
    int increaseStock(@Param("pid") UUID productId, @Param("qty") Integer quantity);
}