package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.exceptions.ResourceNotFoundException;
import com.budgettracker.spendex.models.Category;
import com.budgettracker.spendex.repos.BudgetRepo;
import com.budgettracker.spendex.repos.CategoryRepo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepo categoryRepo;
    private final BudgetRepo budgetRepo;

    public CategoryController(CategoryRepo categoryRepo, BudgetRepo budgetRepo) {
        this.categoryRepo = categoryRepo;
        this.budgetRepo = budgetRepo;
    }

    // CREATE (201 created)
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody @Valid Category category) {
        Category saved = categoryRepo.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // READ (200 ok / 404 not found)
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return ResponseEntity.ok(category);
    }

    // UPDATE (200 ok / 404 not found)
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody @Valid Category categoryDetails) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        category.setName(categoryDetails.getName());
        category.setType(categoryDetails.getType());
        category.setIconUrl(categoryDetails.getIconUrl());

        return ResponseEntity.ok(categoryRepo.save(category));
    }

    // DELETE (204 no content / 404 not found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        categoryRepo.delete(category);
        return ResponseEntity.noContent().build();
    }

    // LIST (all categories)
    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryRepo.findAll());
    }

    // HIERARCHINIS (all categories by budget id)
    @GetMapping("/by-budget/{budgetId}")
    public ResponseEntity<List<Category>> getCategoriesByBudgetId(@PathVariable Long budgetId) {
        var budget = budgetRepo.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));

        return ResponseEntity.ok(categoryRepo.findByBudget(budget));
    }

}