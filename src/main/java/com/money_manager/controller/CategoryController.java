package com.money_manager.controller;

import com.money_manager.dto.request.CategoryRequest;
import com.money_manager.dto.response.CategoryResponse;
import com.money_manager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> savedCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse saveCategory = categoryService.saveCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategoriesByCurrentUser() {
        List<CategoryResponse> responses = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByTypeAndCurrentUser(@PathVariable("type") String type) {
        List<CategoryResponse> responses = categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryResponse response = categoryService.updateCategory(categoryId, categoryRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
