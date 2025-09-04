package com.money_manager.service;

import com.money_manager.dto.request.CategoryRequest;
import com.money_manager.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse saveCategory(CategoryRequest categoryRequest);

    List<CategoryResponse> getCategoriesForCurrentUser();

    List<CategoryResponse> getCategoriesByTypeForCurrentUser(String type);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest);
}
