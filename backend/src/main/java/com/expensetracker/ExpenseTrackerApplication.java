package com.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for Expense Tracker
 * 
 * This application provides REST APIs for managing daily balance entries
 * and calculating expenses based on consecutive day balances.
 */
@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Expense Tracker Application Started!");
        System.out.println("  API Base URL: http://localhost:8080/api");
        System.out.println("  Health Check: http://localhost:8080/api/health");
        System.out.println("========================================\n");
    }
}
