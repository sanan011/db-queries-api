package com.example.dbqueries.controller;

import com.example.dbqueries.dto.CategoryRequest;
import com.example.dbqueries.dto.CategoryResponse;
import com.example.dbqueries.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponse createCategory(@RequestBody CategoryRequest request) {
        return CategoryResponse.from(categoryService.createCategory(request));
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
}

