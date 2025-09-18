package com.budgettracker.spendex.repos;

import com.budgettracker.spendex.models.Budget;
import com.budgettracker.spendex.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category,Long> {

    List<Category> findByBudget(Budget budget);
}
