package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.exceptions.ResourceNotFoundException;
import com.budgettracker.spendex.models.Budget;
import com.budgettracker.spendex.repos.BudgetRepo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetRepo budgetRepo;

    public BudgetController(BudgetRepo budgetRepo) {
        this.budgetRepo = budgetRepo;
    }

    // CREATE (201 created)
    @PostMapping
    public ResponseEntity<Budget> addBudget(@RequestBody @Valid Budget budget) {
        Budget saved = budgetRepo.save(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // READ (200 ok / 404 not found)
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        Budget budget = budgetRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));
        return ResponseEntity.ok(budget);
    }

    // UPDATE (200 ok / 404 not found)
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody @Valid Budget budgetDetails) {
        Budget budget = budgetRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));

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

        budgetRepo.delete(budget);
        return ResponseEntity.noContent().build();
    }

    // LIST (all budgets)
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        return ResponseEntity.ok(budgetRepo.findAll());
    }

}
