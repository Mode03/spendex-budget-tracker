package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.dto.UserProfileUpdateDTO;
import com.budgettracker.spendex.models.User;
import com.budgettracker.spendex.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/testUser")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello form User Controller");
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(@RequestBody UserProfileUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateProfile(dto));
    }

    // tik admin
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> toggleUser(@PathVariable Long id, @RequestParam boolean enabled) {
        return ResponseEntity.ok(userService.toggleUser(id, enabled));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
