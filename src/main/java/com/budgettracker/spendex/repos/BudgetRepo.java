package com.budgettracker.spendex.repos;

import com.budgettracker.spendex.models.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepo extends JpaRepository<Budget,Long> {
}
