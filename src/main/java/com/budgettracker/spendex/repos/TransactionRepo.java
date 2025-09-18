package com.budgettracker.spendex.repos;

import com.budgettracker.spendex.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    List<Transaction> findByCategory(Category category);
}
