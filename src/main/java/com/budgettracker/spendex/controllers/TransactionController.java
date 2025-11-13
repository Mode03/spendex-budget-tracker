package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.exceptions.ForbiddenException;
import com.budgettracker.spendex.exceptions.ResourceNotFoundException;
import com.budgettracker.spendex.models.*;
import com.budgettracker.spendex.repos.CategoryRepo;
import com.budgettracker.spendex.repos.TransactionRepo;
import com.budgettracker.spendex.repos.UserRepo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepo transactionRepo;
    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;

    public TransactionController(TransactionRepo transactionRepo, CategoryRepo categoryRepo, UserRepo userRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
        this.userRepo = userRepo;
    }

    // CREATE (201 created)
    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody @Valid Transaction transaction) {
        var category = categoryRepo.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", transaction.getCategory().getId()));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !category.getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can add transactions only to your own categories!");
        }

        Transaction saved = transactionRepo.save(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // READ (200 ok / 404 not found)
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !transaction.getCategory().getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can view only your own transactions!");
        }

        return ResponseEntity.ok(transaction);
    }

    // UPDATE (200 ok / 404 not found)
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable long id, @RequestBody @Valid Transaction transactionDetails) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !transaction.getCategory().getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can update only your own transactions!");
        }

        transaction.setAmount(transactionDetails.getAmount());
        transaction.setDate(transactionDetails.getDate());
        transaction.setDescription(transactionDetails.getDescription());

        return ResponseEntity.ok(transactionRepo.save(transaction));
    }

    // DELETE (204 no content / 404 not found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !transaction.getCategory().getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can delete only your own transactions!");
        }

        transactionRepo.delete(transaction);
        return ResponseEntity.noContent().build();
    }

    // LIST (all transactions)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionRepo.findAll());
    }

    // HIERARCHINIS (all transactions by category)
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByCategory(@PathVariable long categoryId) {
        var category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !category.getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You cannot view transactions in this category!");
        }

        return ResponseEntity.ok(transactionRepo.findByCategory(category));
    }

}
