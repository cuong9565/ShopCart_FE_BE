package com.shopcart.service;

import java.util.List;
import java.util.UUID;

import com.shopcart.dto.ProductDetailResponseDTO;
import com.shopcart.dto.ProductResponseDTO;

public interface ProductService {
    List<ProductResponseDTO> getAllProducts();
    List<ProductResponseDTO> getFeaturedProducts();
    ProductDetailResponseDTO getProductDetailById(UUID id);
}
