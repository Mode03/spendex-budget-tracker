package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.models.Budget;
import com.budgettracker.spendex.repos.BudgetRepo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return budgetRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE (200 ok / 404 not found)
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody @Valid Budget budgetDetails) {
        return budgetRepo.findById(id).map(budget -> {
            budget.setName(budgetDetails.getName());
            budget.setDescription(budgetDetails.getDescription());
            budget.setStartDate(budgetDetails.getStartDate());
            budget.setEndDate(budgetDetails.getEndDate());
            return ResponseEntity.ok(budgetRepo.save(budget));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE (204 no content / 404 not found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        return budgetRepo.findById(id).map(budget -> {
            budgetRepo.delete(budget);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // LIST (all budgets)
    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetRepo.findAll();
    }

}
