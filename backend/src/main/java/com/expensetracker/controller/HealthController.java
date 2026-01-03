package com.expensetracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * Provides API endpoint to check backend and database connectivity
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * GET /api/health - Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Expense Tracker API is running");
        response.put("timestamp", System.currentTimeMillis());

        // Check database connection
        try (Connection connection = dataSource.getConnection()) {
            response.put("database", "Connected");
            response.put("databaseProduct", connection.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            response.put("database", "Disconnected");
            response.put("databaseError", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/info - API information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Expense Tracker API");
        info.put("version", "1.0.0");
        info.put("description", "REST API for managing daily balance entries and calculating expenses");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/health", "Health check");
        endpoints.put("GET /api/balances", "Get all balance entries");
        endpoints.put("GET /api/balances/{id}", "Get entry by ID");
        endpoints.put("GET /api/balances/date/{date}", "Get entry by date");
        endpoints.put("POST /api/balances", "Create new entry");
        endpoints.put("PUT /api/balances/{id}", "Update entry");
        endpoints.put("DELETE /api/balances/{id}", "Delete entry");
        endpoints.put("DELETE /api/balances/clear", "Delete all entries");
        endpoints.put("GET /api/balances/range", "Get entries by date range");
        endpoints.put("GET /api/balances/month", "Get entries by month");
        endpoints.put("GET /api/balances/summary", "Get expense summary");
        endpoints.put("GET /api/balances/monthly-summary", "Get monthly summary");
        endpoints.put("GET /api/balances/expense/{date}", "Calculate daily expense");
        
        info.put("endpoints", endpoints);
        return ResponseEntity.ok(info);
    }
}
