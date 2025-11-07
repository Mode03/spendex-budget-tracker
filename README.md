# Spendex - personal budget tracker

**Spendex** is a RESTful application built with **Spring Boot**, designed to help users manage personal finances efficiently.
It provides secure **JWT authentication**, **role-based authorization**, and CRUD APIs for **budgets**, **categories**, and **transactions**.

## 🚀 Features

### Authentication & Security
- Secure login and registration with JWT-based authentication
- Supports access and refresh tokens
- Stateless session management for scalability
- Role-based authorization (USER, ADMIN)
- Custom JWT filter and validation on every request

### User Management
- Register and authenticate users with encrypted passwords (BCrypt)
- Validate credentials and refresh tokens

### Budget Management
- Create, update, and delete budgets linked to users
- Each budget can have multiple categories and transactions
- Automatically stores creation and update timestamps

### Category Management
- Add, edit, or remove categories for budgets
- Supports both income and expense types
- Categorize transactions for better financial tracking

### Transaction Tracking
- Add new income or expense transactions
- Validate transaction amounts and dates
- Retrieve transactions by category or across all budgets

### Additional Features
- Global exception handling for consistent API responses
- Input validation using Jakarta Validation annotations
- Easily extendable for new modules like investments or goals

## ⚙️ Tech Stack
- Java 21
- Spring Boot
- PostgreSQL
