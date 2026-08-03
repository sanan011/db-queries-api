package com.example.dbqueries.service;

import com.example.dbqueries.dto.CategoryRequest;
import com.example.dbqueries.entity.Category;
import com.example.dbqueries.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
