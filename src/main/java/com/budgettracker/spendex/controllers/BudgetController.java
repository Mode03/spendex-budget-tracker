package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.models.Budget;
import com.budgettracker.spendex.repos.BudgetRepo;
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

    // CREATE
    @PostMapping
    public Budget addBudget(@RequestBody Budget budget) {
        return budgetRepo.save(budget);
    }

    // READ (by id)
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return budgetRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budgetDetails) {
        return budgetRepo.findById(id).map(budget -> {
            budget.setName(budgetDetails.getName());
            budget.setDescription(budgetDetails.getDescription());
            budget.setStartDate(budgetDetails.getStartDate());
            budget.setEndDate(budgetDetails.getEndDate());
            return ResponseEntity.ok(budgetRepo.save(budget));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        return budgetRepo.findById(id).map(budget -> {
            budgetRepo.delete(budget);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // LIST (all budgets)
    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetRepo.findAll();
    }

}
