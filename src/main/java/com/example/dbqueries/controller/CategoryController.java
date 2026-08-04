package com.example.dbqueries.controller;

import com.example.dbqueries.dto.CategoryRequest;
import com.example.dbqueries.dto.CategoryResponse;
import com.example.dbqueries.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a category", description = "Creates a new book category")
    public CategoryResponse createCategory(@RequestBody CategoryRequest request) {
        return CategoryResponse.from(categoryService.createCategory(request));
    }

    @GetMapping
    @Operation(summary = "List all categories", description = "Returns every category in the catalog")
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
}

