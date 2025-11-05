package com.budgettracker.spendex.repos;

import com.budgettracker.spendex.models.Role;
import com.budgettracker.spendex.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByRole(Role role);
}
