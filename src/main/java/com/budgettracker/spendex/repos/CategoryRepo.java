package com.budgettracker.spendex.repos;

import com.budgettracker.spendex.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category,Long> {
}
