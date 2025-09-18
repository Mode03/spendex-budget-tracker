package com.budgettracker.spendex.services;

import com.budgettracker.spendex.models.User;
import com.budgettracker.spendex.repos.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User registerUser(User user) {
        return userRepo.save(user);
    }
}
