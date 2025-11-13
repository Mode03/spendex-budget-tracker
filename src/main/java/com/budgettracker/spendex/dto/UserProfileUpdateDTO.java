package com.budgettracker.spendex.dto;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private String password;
}
