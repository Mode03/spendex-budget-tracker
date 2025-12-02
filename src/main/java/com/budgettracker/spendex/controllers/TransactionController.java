package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.models.*;
import com.budgettracker.spendex.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // CREATE TRANSACTION
    @Operation(
            summary = "Create a new transaction",
            description = "Creates a new transaction in a category. Users can add transactions only to their own categories unless ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "Malformed JSON or invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot add transaction to another user's category")
    })
    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody @Valid Transaction transaction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transaction));
    }

    // GET TRANSACTION BY ID
    @Operation(
            summary = "Get transaction by ID",
            description = "Returns a transaction by its ID. Users can only view their own transactions unless ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot view another user's transaction"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable long id) {
        return ResponseEntity.ok(transactionService.getTransaction(id));
    }

    // UPDATE TRANSACTION
    @Operation(
            summary = "Update an existing transaction",
            description = "Updates a transaction. Users can update only their own transactions unless ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot update another user's transaction"),
            @ApiResponse(responseCode = "422", description = "Validation failed"),
            @ApiResponse(responseCode = "400", description = "Malformed request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable long id, @RequestBody @Valid Transaction transactionDetails) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionDetails));
    }

    // DELETE TRANSACTION
    @Operation(
            summary = "Delete transaction",
            description = "Deletes a transaction. Only the owner or ADMIN can delete it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot delete another user's transaction"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    // LIST ALL TRANSACTIONS (ADMIN only)
    @Operation(
            summary = "Get all transactions",
            description = "Returns all transactions. Only accessible to ADMIN users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - only ADMIN can access")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    // LIST TRANSACTIONS BY CATEGORY
    @Operation(
            summary = "Get transactions by category ID",
            description = "Returns all transactions for a specific category. Users can only view their own category transactions unless ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot view another user's category transactions"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid category ID format")
    })
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByCategory(@PathVariable long categoryId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCategory(categoryId));
    }

}
