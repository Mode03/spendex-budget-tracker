package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.models.Budget;
import com.budgettracker.spendex.services.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // CREATE (201 created)
    @PostMapping
    public ResponseEntity<Budget> addBudget(@RequestBody @Valid Budget budget) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(budget));
    }

    // READ (200 ok / 404 not found)
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudget(id));
    }

    // UPDATE (200 ok / 404 not found)
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody @Valid Budget budgetDetails) {
        return ResponseEntity.ok(budgetService.updateBudget(id, budgetDetails));
    }

    // DELETE (204 no content / 404 not found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    // LIST (all budgets)
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        return  ResponseEntity.ok(budgetService.getAllBudgets());
    }

}
