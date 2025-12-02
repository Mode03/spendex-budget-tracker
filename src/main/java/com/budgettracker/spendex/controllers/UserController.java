package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.dto.UserProfileUpdateDTO;
import com.budgettracker.spendex.models.User;
import com.budgettracker.spendex.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get current user's profile",
            description = "Returns the profile of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - no or invalid token")
    })
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @Operation(
            summary = "Update current user's profile",
            description = "Updates the profile fields (firstName, lastName, password) of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(@RequestBody UserProfileUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateProfile(dto));
    }

    // ADMIN only
    @Operation(
            summary = "Toggle user enabled/disabled",
            description = "Admin can enable or disable a user by ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User toggled successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - only admin can toggle users"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> toggleUser(@PathVariable Long id, @RequestParam boolean enabled) {
        return ResponseEntity.ok(userService.toggleUser(id, enabled));
    }

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users. Only accessible by ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - only admin can access"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
