package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.models.Category;
import com.budgettracker.spendex.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE CATEGORY
    @Operation(
            summary = "Create a new category",
            description = "Creates a new category in a budget. Users can create categories only in their own budgets unless ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "Malformed JSON or invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot add category to another user's budget")
    })
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody @Valid Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(category));
    }

    // GET CATEGORY BY ID
    @Operation(
            summary = "Get category by ID",
            description = "Returns a category by its ID. Users can only view their own categories unless they are ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot view another user's category"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    // UPDATE CATEGORY
    @Operation(
            summary = "Update an existing category",
            description = "Updates an existing category. Users can update only their own categories unless ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot update another user's category"),
            @ApiResponse(responseCode = "422", description = "Validation failed"),
            @ApiResponse(responseCode = "400", description = "Malformed request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody @Valid Category categoryDetails) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDetails));
    }

    // DELETE CATEGORY
    @Operation(
            summary = "Delete category",
            description = "Deletes a category. Only the owner or ADMIN can delete it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot delete another user's category"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {

        Category category = categoryService.getCategory(id);
        Long budgetId = category.getBudget().getId();

        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("budgetId", budgetId));
    }

    // LIST ALL CATEGORIES (ADMIN only)
    @Operation(
            summary = "Get all categories",
            description = "Returns all categories. Only accessible to ADMIN users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of categories retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - only ADMIN can access")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // LIST CATEGORIES BY BUDGET
    @Operation(
            summary = "Get categories by budget ID",
            description = "Returns all categories for a specific budget. Users can only view their own budget categories unless ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - cannot view another user's budget categories"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid budget ID format")
    })
    @GetMapping("/by-budget/{budgetId}")
    public ResponseEntity<List<Category>> getCategoriesByBudgetId(@PathVariable Long budgetId) {
        return ResponseEntity.ok(categoryService.getCategoriesByBudget(budgetId));
    }

}