package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.exceptions.ForbiddenException;
import com.budgettracker.spendex.exceptions.ResourceNotFoundException;
import com.budgettracker.spendex.models.Budget;
import com.budgettracker.spendex.models.Role;
import com.budgettracker.spendex.repos.BudgetRepo;
import com.budgettracker.spendex.repos.UserRepo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.budgettracker.spendex.models.User;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetRepo budgetRepo;
    private final UserRepo userRepo;

    public BudgetController(BudgetRepo budgetRepo, UserRepo userRepo) {
        this.budgetRepo = budgetRepo;
        this.userRepo = userRepo;
    }

    // CREATE (201 created)
    @PostMapping
    public ResponseEntity<Budget> addBudget(@RequestBody @Valid Budget budget) {
        User currentUser = getCurrentUser();
        budget.setUser(currentUser);
        Budget saved = budgetRepo.save(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // READ (200 ok / 404 not found)
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        Budget budget = budgetRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can view only your own budgets!");
        }

        return ResponseEntity.ok(budget);
    }

    // UPDATE (200 ok / 404 not found)
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody @Valid Budget budgetDetails) {
        Budget budget = budgetRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can update only your own budgets!");
        }

        budget.setName(budgetDetails.getName());
        budget.setDescription(budgetDetails.getDescription());
        budget.setStartDate(budgetDetails.getStartDate());
        budget.setEndDate(budgetDetails.getEndDate());

        return ResponseEntity.ok(budgetRepo.save(budget));
    }

    // DELETE (204 no content / 404 not found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        Budget budget = budgetRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));

        User currentUser = getCurrentUser();
        if (!budget.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("You can delete only your own budgets!");
        }

        budgetRepo.delete(budget);
        return ResponseEntity.noContent().build();
    }

    // LIST (all budgets)
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return ResponseEntity.ok(budgetRepo.findAll());
        } else {
            return ResponseEntity.ok(budgetRepo.findByUser(currentUser));
        }
    }

}
