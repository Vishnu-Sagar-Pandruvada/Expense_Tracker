package com.expensetracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

/**
 * Data Transfer Object for Balance Entry
 * Used for API request/response to separate entity from external representation
 */
public class BalanceEntryDTO {

    private Long id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Balance is required")
    @PositiveOrZero(message = "Balance must be zero or positive")
    private Double balance;

    private String description;

    // Calculated field - expense compared to previous day
    private Double dailyExpense;

    // Constructors
    public BalanceEntryDTO() {
    }

    public BalanceEntryDTO(Long id, LocalDate date, Double balance, String description) {
        this.id = id;
        this.date = date;
        this.balance = balance;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDailyExpense() {
        return dailyExpense;
    }

    public void setDailyExpense(Double dailyExpense) {
        this.dailyExpense = dailyExpense;
    }
}
