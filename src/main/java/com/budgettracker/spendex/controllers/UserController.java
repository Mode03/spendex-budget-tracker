package com.budgettracker.spendex.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping("/testUser")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello form User Controller");
    }
}
