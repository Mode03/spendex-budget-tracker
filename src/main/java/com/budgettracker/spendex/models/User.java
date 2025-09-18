package com.budgettracker.spendex.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    //private Role role; // Enum: guest, user, admin
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // rysiai budgets
}
