package com.money_manager.service;

import com.money_manager.dto.request.CategoryRequest;
import com.money_manager.dto.response.CategoryResponse;
import com.money_manager.dto.response.ProfileResponse;
import com.money_manager.entity.Category;
import com.money_manager.entity.Profile;
import com.money_manager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ProfileService profileService;

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse saveCategory(CategoryRequest categoryRequest) {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryRequest.getName(), profileResponse.getId())) {
            throw new RuntimeException("Category with this name already exists");
        }
        Category category = mapCategoryRequestToCategory(categoryRequest, profileResponse);
        Category savedCategory = categoryRepository.save(category);
        return mapCategoryToCategoryResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getCategoriesForCurrentUser() {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        return categoryRepository.findByProfileId(profileResponse.getId())
                .stream().map(this::mapCategoryToCategoryResponse).toList();
    }

    @Override
    public List<CategoryResponse> getCategoriesByTypeForCurrentUser(String type) {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        return categoryRepository.findByTypeAndProfileId(type, profileResponse.getId())
                .stream().map(this::mapCategoryToCategoryResponse).toList();
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        Category category = categoryRepository.findByIdAndProfileId(categoryId, profileResponse.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or not accessible"));
        category.setName(categoryRequest.getName());
        category.setIcon(categoryRequest.getIcon());
        category.setType(categoryRequest.getType());
        Category savedCategories = categoryRepository.save(category);
        return mapCategoryToCategoryResponse(savedCategories);
    }

    private Category mapCategoryRequestToCategory(CategoryRequest categoryRequest, ProfileResponse profileResponse) {
        return Category.builder()
                .name(categoryRequest.getName())
                .icon(categoryRequest.getIcon())
                .profile(this.profileSaved(profileResponse))
                .type(categoryRequest.getType())
                .build();
    }

    private CategoryResponse mapCategoryToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .profileId(category.getProfile() != null ? category.getProfile().getId() : null)
                .name(category.getName())
                .icon(category.getIcon())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .type(category.getType())
                .build();
    }

    private Profile profileSaved(ProfileResponse profileResponse) {
        return Profile.builder()
                .id(profileResponse.getId())
                .fullName(profileResponse.getFullName())
                .email(profileResponse.getEmail())
                .profileImageUrl(profileResponse.getProfileImageUrl())
                .build();
    }
}
