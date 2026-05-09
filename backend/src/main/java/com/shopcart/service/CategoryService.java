package com.shopcart.service;

import java.util.List;

import com.shopcart.dto.CategoryResponseDTO;

public interface CategoryService {
    List<CategoryResponseDTO> getAllCategories();
}
