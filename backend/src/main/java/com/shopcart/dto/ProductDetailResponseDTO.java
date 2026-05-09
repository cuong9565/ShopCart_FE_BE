package com.shopcart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String description;
    private String status;
    private String slug;
    private LocalDateTime createdAt;
    private CategoryResponseDTO category;
    private Integer stockQuantity;
    private List<String> images;
}
