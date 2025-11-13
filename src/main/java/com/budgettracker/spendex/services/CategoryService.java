package com.budgettracker.spendex.services;

import com.budgettracker.spendex.exceptions.ForbiddenException;
import com.budgettracker.spendex.exceptions.ResourceNotFoundException;
import com.budgettracker.spendex.models.Category;
import com.budgettracker.spendex.models.Role;
import com.budgettracker.spendex.models.User;
import com.budgettracker.spendex.repos.BudgetRepo;
import com.budgettracker.spendex.repos.CategoryRepo;
import com.budgettracker.spendex.repos.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final BudgetRepo budgetRepo;
    private final UserRepo userRepo;

    public CategoryService(CategoryRepo categoryRepo, BudgetRepo budgetRepo, UserRepo userRepo) {
        this.categoryRepo = categoryRepo;
        this.budgetRepo = budgetRepo;
        this.userRepo = userRepo;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Category createCategory(Category category) {
        var budget = budgetRepo.findById(category.getBudget().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget", category.getBudget().getId()));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can add categories only to your own budgets!");
        }

        return categoryRepo.save(category);
    }

    public Category getCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !category.getBudget().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can view only your own categories!");
        }

        return category;
    }

    public Category updateCategory(Long id, Category details) {
        Category category = getCategory(id);

        category.setName(details.getName());
        category.setType(details.getType());
        category.setIconUrl(details.getIconUrl());
        return categoryRepo.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategory(id);
        categoryRepo.delete(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll(); // tik admin
    }

    public List<Category> getCategoriesByBudget(Long budgetId) {
        var budget = budgetRepo.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN &&
                !budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can view categories only for your own budgets!");
        }

        return categoryRepo.findByBudget(budget);
    }
}
