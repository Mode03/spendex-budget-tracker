package com.budgettracker.spendex.services;

import com.budgettracker.spendex.exceptions.ForbiddenException;
import com.budgettracker.spendex.exceptions.ResourceNotFoundException;
import com.budgettracker.spendex.models.Budget;
import com.budgettracker.spendex.models.Role;
import com.budgettracker.spendex.models.User;
import com.budgettracker.spendex.repos.BudgetRepo;
import com.budgettracker.spendex.repos.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepo budgetRepo;
    private final UserRepo userRepo;

    public BudgetService(BudgetRepo budgetRepo, UserRepo userRepo) {
        this.budgetRepo = budgetRepo;
        this.userRepo = userRepo;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Budget createBudget(Budget budget) {
        User currentUser = getCurrentUser();
        budget.setUser(currentUser);
        return budgetRepo.save(budget);
    }

    public Budget getBudget(Long id) {
        Budget budget = budgetRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !budget.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can view only your own budgets!");
        }

        return budget;
    }

    public Budget updateBudget(Long id, Budget details) {
        Budget budget = getBudget(id);

        budget.setName(details.getName());
        budget.setDescription(details.getDescription());
        budget.setStartDate(details.getStartDate());
        budget.setEndDate(details.getEndDate());
        return budgetRepo.save(budget);
    }

    public void deleteBudget(Long id) {
        Budget budget = getBudget(id);
        budgetRepo.delete(budget);
    }

    public List<Budget> getAllBudgets() {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return budgetRepo.findAll();
        } else {
            return budgetRepo.findByUser(currentUser);
        }
    }
}
