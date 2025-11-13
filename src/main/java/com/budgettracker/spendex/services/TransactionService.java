package com.budgettracker.spendex.services;

import com.budgettracker.spendex.exceptions.ForbiddenException;
import com.budgettracker.spendex.exceptions.ResourceNotFoundException;
import com.budgettracker.spendex.models.Role;
import com.budgettracker.spendex.models.Transaction;
import com.budgettracker.spendex.models.User;
import com.budgettracker.spendex.repos.CategoryRepo;
import com.budgettracker.spendex.repos.TransactionRepo;
import com.budgettracker.spendex.repos.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;

    public TransactionService(TransactionRepo transactionRepo, CategoryRepo categoryRepo, UserRepo userRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
        this.userRepo = userRepo;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Transaction createTransaction(Transaction transaction) {
        var category = categoryRepo.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", transaction.getCategory().getId()));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !category.getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can add transactions only to your own categories!");
        }

        return transactionRepo.save(transaction);
    }

    public Transaction getTransaction(Long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !transaction.getCategory().getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can view only your own transactions!");
        }

        return transaction;
    }

    public Transaction updateTransaction(Long id, Transaction details) {
        Transaction transaction = getTransaction(id);

        transaction.setAmount(details.getAmount());
        transaction.setDate(details.getDate());
        transaction.setDescription(details.getDescription());
        return transactionRepo.save(transaction);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = getTransaction(id);
        transactionRepo.delete(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll(); // tik admin
    }

    public List<Transaction> getTransactionsByCategory(Long categoryId) {
        var category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !category.getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You cannot view transactions in this category!");
        }

        return transactionRepo.findByCategory(category);
    }
}
