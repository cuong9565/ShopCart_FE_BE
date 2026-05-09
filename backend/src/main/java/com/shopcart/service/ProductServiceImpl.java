package com.shopcart.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.shopcart.dto.CategoryResponseDTO;
import com.shopcart.dto.ProductDetailResponseDTO;
import com.shopcart.dto.ProductResponseDTO;
import com.shopcart.entity.Inventory;
import com.shopcart.entity.Product;
import com.shopcart.entity.ProductImage;
import com.shopcart.repository.InventoryRepository;
import com.shopcart.repository.ProductImageRepository;
import com.shopcart.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductImageRepository productImageRepository;
    
    @Override
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponseDTO> getFeaturedProducts() {
        List<Product> products = productRepository.findByIsFeatured(true);
        return products.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }   
    
    @Override
    public ProductDetailResponseDTO getProductDetailById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return convertToDetailDTO(product);
    }
    
    private ProductResponseDTO convertToResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStatus(product.getStatus());
        dto.setSlug(product.getSlug());
        dto.setCreatedAt(product.getCreatedAt());
        
        // Get stock quantity from inventory
        Inventory inventory = inventoryRepository.findByProductId(product.getId());
        if (inventory != null) {
            dto.setStockQuantity(inventory.getQuantity());
        } else {
            dto.setStockQuantity(0);
        }
        
        // Get thumbnail image
        ProductImage thumbnail = productImageRepository.findThumbnailByProductId(product.getId());
        if (thumbnail != null) {
            dto.setThumbnailImage(thumbnail.getImageUrl());
        }
        
        if (product.getCategory() != null) {
            CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
            categoryDTO.setId(product.getCategory().getId());
            categoryDTO.setName(product.getCategory().getName());
            categoryDTO.setCreatedAt(product.getCategory().getCreatedAt());
            dto.setCategory(categoryDTO);
        }
        
        return dto;
    }
    
    private ProductDetailResponseDTO convertToDetailDTO(Product product) {
        ProductDetailResponseDTO dto = new ProductDetailResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setStatus(product.getStatus());
        dto.setSlug(product.getSlug());
        dto.setCreatedAt(product.getCreatedAt());
        
        // Get stock quantity from inventory
        Inventory inventory = inventoryRepository.findByProductId(product.getId());
        if (inventory != null) {
            dto.setStockQuantity(inventory.getQuantity());
        } else {
            dto.setStockQuantity(0);
        }
        
        // Get all images for product
        List<ProductImage> images = productImageRepository.findImagesByProductIdOrdered(product.getId());
        List<String> imageUrls = images.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
        dto.setImages(imageUrls);
        
        if (product.getCategory() != null) {
            CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
            categoryDTO.setId(product.getCategory().getId());
            categoryDTO.setName(product.getCategory().getName());
            categoryDTO.setCreatedAt(product.getCategory().getCreatedAt());
            dto.setCategory(categoryDTO);
        }
        
        return dto;
    }
}
