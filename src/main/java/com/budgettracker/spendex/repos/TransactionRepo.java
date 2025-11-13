package com.budgettracker.spendex.repos;

import com.budgettracker.spendex.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    List<Transaction> findByCategory(Category category);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.category.budget.user = :user")
    List<Transaction> findByUser(@Param("user") User user);
}
