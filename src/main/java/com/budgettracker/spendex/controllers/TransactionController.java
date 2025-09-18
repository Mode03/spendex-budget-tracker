package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.models.*;
import com.budgettracker.spendex.repos.CategoryRepo;
import com.budgettracker.spendex.repos.TransactionRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepo transactionRepo;
    private final CategoryRepo categoryRepo;

    public TransactionController(TransactionRepo transactionRepo, CategoryRepo categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
    }

    // CREATE
    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    // READ (by id)
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable long id) {
        return transactionRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable long id, @RequestBody Transaction transactionDetails) {
        return transactionRepo.findById(id).map(transaction -> {
            transaction.setAmount(transactionDetails.getAmount());
            transaction.setType(transactionDetails.getType());
            transaction.setDate(transactionDetails.getDate());
            transaction.setDescription(transactionDetails.getDescription());
            return ResponseEntity.ok(transactionRepo.save(transaction));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable long id) {
        return transactionRepo.findById(id).map(transaction -> {
            transactionRepo.delete(transaction);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // LIST (all transactions)
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    // HIERARCHINIS (all transactions by category)
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByCategory(@PathVariable long categoryId) {
        return categoryRepo.findById(categoryId).map(category ->
                ResponseEntity.ok(transactionRepo.findByCategory(category))
        ).orElse(ResponseEntity.notFound().build());
    }

}
