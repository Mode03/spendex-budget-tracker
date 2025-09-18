package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.models.Category;
import com.budgettracker.spendex.repos.BudgetRepo;
import com.budgettracker.spendex.repos.CategoryRepo;
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

    // CREATE
    @PostMapping
    public Category addCategory(@RequestBody Category category) {
        return categoryRepo.save(category);
    }

    // READ (by id)
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        return categoryRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        return categoryRepo.findById(id).map(category -> {
            category.setName(categoryDetails.getName());
            category.setType(categoryDetails.getType());
            return ResponseEntity.ok(categoryRepo.save(category));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        return categoryRepo.findById(id).map( category -> {
            categoryRepo.delete(category);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // LIST (all categories)
    @GetMapping
    public List<Category> getCategories() {
        return categoryRepo.findAll();
    }

    // HIERARCHINIS (all categories by budget id)
    @GetMapping("/by-budget/{budgetId}")
    public ResponseEntity<List<Category>> getCategoriesByBudgetId(@PathVariable Long budgetId) {
        return budgetRepo.findById(budgetId).map(budget ->
                ResponseEntity.ok(categoryRepo.findByBudget(budget))
        ).orElse(ResponseEntity.notFound().build());
    }

}