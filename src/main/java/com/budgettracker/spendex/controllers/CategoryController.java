package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.exceptions.ForbiddenException;
import com.budgettracker.spendex.exceptions.ResourceNotFoundException;
import com.budgettracker.spendex.models.Category;
import com.budgettracker.spendex.models.Role;
import com.budgettracker.spendex.repos.BudgetRepo;
import com.budgettracker.spendex.repos.CategoryRepo;
import com.budgettracker.spendex.repos.UserRepo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.budgettracker.spendex.models.User;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepo categoryRepo;
    private final BudgetRepo budgetRepo;
    private final UserRepo userRepo;

    public CategoryController(CategoryRepo categoryRepo, BudgetRepo budgetRepo, UserRepo userRepo) {
        this.categoryRepo = categoryRepo;
        this.budgetRepo = budgetRepo;
        this.userRepo = userRepo;
    }

    // CREATE (201 created)
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody @Valid Category category) {
        var budget = budgetRepo.findById(category.getBudget().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget", category.getBudget().getId()));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can add categories only to your own budgets!");
        }

        Category saved = categoryRepo.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // READ (200 ok / 404 not found)
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !category.getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can view only your own categories!");
        }

        return ResponseEntity.ok(category);
    }

    // UPDATE (200 ok / 404 not found)
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody @Valid Category categoryDetails) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !category.getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can edit categories only in your own budgets!");
        }

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

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !category.getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can delete categories only from your own budgets!");
        }

        categoryRepo.delete(category);
        return ResponseEntity.noContent().build();
    }

    // LIST (all categories)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryRepo.findAll());
    }

    // HIERARCHINIS (all categories by budget id)
    @GetMapping("/by-budget/{budgetId}")
    public ResponseEntity<List<Category>> getCategoriesByBudgetId(@PathVariable Long budgetId) {
        var budget = budgetRepo.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can view categories only for your own budgets!");
        }

        return ResponseEntity.ok(categoryRepo.findByBudget(budget));
    }

}