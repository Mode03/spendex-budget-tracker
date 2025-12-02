package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.models.Budget;
import com.budgettracker.spendex.services.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // CREATE BUDGET
    @Operation(
            summary = "Create a new budget",
            description = "Creates a new budget for the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Budget created successfully"),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "Malformed JSON or invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - no or invalid token")
    })
    @PostMapping
    public ResponseEntity<Budget> addBudget(@RequestBody @Valid Budget budget) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(budget));
    }

    // GET BUDGET BY ID
    @Operation(
            summary = "Get budget by ID",
            description = "Returns a budget by its ID. Users can only view their own budgets unless they are ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user tried to access another user's budget"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudget(id));
    }

    // UPDATE BUDGET
    @Operation(
            summary = "Update an existing budget",
            description = "Updates an existing budget. Users can update only their own budgets unless they are ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget updated successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot update another user's budget"),
            @ApiResponse(responseCode = "422", description = "Validation failed"),
            @ApiResponse(responseCode = "400", description = "Malformed request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody @Valid Budget budgetDetails) {
        return ResponseEntity.ok(budgetService.updateBudget(id, budgetDetails));
    }

    // DELETE BUDGET
    @Operation(
            summary = "Delete budget",
            description = "Deletes a budget. Only the owner or ADMIN can delete it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot delete another user's budget"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    // LIST BUDGETS
    @Operation(
            summary = "Get all budgets",
            description = "Returns all budgets visible to the current user. Normal users see only their own. ADMIN sees all."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of budgets"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        return  ResponseEntity.ok(budgetService.getAllBudgets());
    }

}
