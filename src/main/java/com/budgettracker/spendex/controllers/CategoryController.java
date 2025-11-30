package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.models.Category;
import com.budgettracker.spendex.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE (201 created)
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody @Valid Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(category));
    }

    // READ (200 ok / 404 not found)
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    // UPDATE (200 ok / 404 not found)
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody @Valid Category categoryDetails) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDetails));
    }

    // DELETE (204 no content / 404 not found)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {

        Category category = categoryService.getCategory(id);
        Long budgetId = category.getBudget().getId();

        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("budgetId", budgetId));
    }

    // LIST (all categories)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // HIERARCHINIS (all categories by budget id)
    @GetMapping("/by-budget/{budgetId}")
    public ResponseEntity<List<Category>> getCategoriesByBudgetId(@PathVariable Long budgetId) {
        return ResponseEntity.ok(categoryService.getCategoriesByBudget(budgetId));
    }

}