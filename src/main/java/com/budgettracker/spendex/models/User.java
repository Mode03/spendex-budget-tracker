package com.budgettracker.spendex.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password;
    //private Role role; // Enum: guest, user, admin
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // rysiai budgets
}
