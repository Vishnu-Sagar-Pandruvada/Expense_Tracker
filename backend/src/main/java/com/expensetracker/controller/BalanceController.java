package com.expensetracker.controller;

import com.expensetracker.dto.BalanceEntryDTO;
import com.expensetracker.dto.ExpenseSummaryDTO;
import com.expensetracker.service.BalanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Balance Entry operations
 * Provides CRUD endpoints and expense calculation APIs
 */
@RestController
@RequestMapping("/api/balances")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BalanceController {

    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * GET /api/balances - Get all balance entries
     */
    @GetMapping
    public ResponseEntity<List<BalanceEntryDTO>> getAllEntries() {
        List<BalanceEntryDTO> entries = balanceService.getAllEntries();
        return ResponseEntity.ok(entries);
    }

    /**
     * GET /api/balances/{id} - Get entry by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BalanceEntryDTO> getEntryById(@PathVariable Long id) {
        return balanceService.getEntryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/balances/date/{date} - Get entry by date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<BalanceEntryDTO> getEntryByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return balanceService.getEntryByDate(date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/balances - Create a new balance entry
     */
    @PostMapping
    public ResponseEntity<?> createEntry(@Valid @RequestBody BalanceEntryDTO dto) {
        try {
            BalanceEntryDTO created = balanceService.createEntry(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * PUT /api/balances/{id} - Update an existing entry
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntry(@PathVariable Long id, @Valid @RequestBody BalanceEntryDTO dto) {
        try {
            BalanceEntryDTO updated = balanceService.updateEntry(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * DELETE /api/balances/{id} - Delete an entry
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id) {
        try {
            balanceService.deleteEntry(id);
            return ResponseEntity.ok(createSuccessResponse("Entry deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * DELETE /api/balances/clear - Delete all entries
     */
    @DeleteMapping("/clear")
    public ResponseEntity<?> deleteAllEntries() {
        balanceService.deleteAllEntries();
        return ResponseEntity.ok(createSuccessResponse("All entries deleted successfully"));
    }

    /**
     * GET /api/balances/range - Get entries by date range
     */
    @GetMapping("/range")
    public ResponseEntity<List<BalanceEntryDTO>> getEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<BalanceEntryDTO> entries = balanceService.getEntriesByDateRange(startDate, endDate);
        return ResponseEntity.ok(entries);
    }

    /**
     * GET /api/balances/month - Get entries for a specific month
     */
    @GetMapping("/month")
    public ResponseEntity<List<BalanceEntryDTO>> getEntriesByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        List<BalanceEntryDTO> entries = balanceService.getEntriesByMonth(year, month);
        return ResponseEntity.ok(entries);
    }

    /**
     * GET /api/balances/summary - Get expense summary for date range
     */
    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryDTO> getExpenseSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ExpenseSummaryDTO summary = balanceService.getExpenseSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /api/balances/monthly-summary - Get monthly expense summary
     */
    @GetMapping("/monthly-summary")
    public ResponseEntity<ExpenseSummaryDTO> getMonthlySummary(
            @RequestParam int year,
            @RequestParam int month) {
        ExpenseSummaryDTO summary = balanceService.getMonthlySummary(year, month);
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /api/balances/expense/{date} - Calculate expense for a specific date
     */
    @GetMapping("/expense/{date}")
    public ResponseEntity<?> getDailyExpense(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Double expense = balanceService.calculateDailyExpense(date);
        Map<String, Object> response = new HashMap<>();
        response.put("date", date.toString());
        response.put("expense", expense);
        response.put("calculated", expense != null);
        return ResponseEntity.ok(response);
    }

    // Helper methods
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        return response;
    }
}
