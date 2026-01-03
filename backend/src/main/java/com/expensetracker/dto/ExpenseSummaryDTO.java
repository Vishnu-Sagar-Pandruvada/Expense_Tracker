package com.expensetracker.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for expense summary reports
 * Contains aggregated expense data for a date range
 */
public class ExpenseSummaryDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private Double openingBalance;
    private Double closingBalance;
    private Double totalExpenses;
    private Double averageDailyExpense;
    private Integer totalDays;
    private Integer daysWithExpenses;
    private List<DailyExpenseDTO> dailyBreakdown;

    // Inner class for daily breakdown
    public static class DailyExpenseDTO {
        private LocalDate date;
        private Double balance;
        private Double expense;
        private String description;

        public DailyExpenseDTO() {
        }

        public DailyExpenseDTO(LocalDate date, Double balance, Double expense, String description) {
            this.date = date;
            this.balance = balance;
            this.expense = expense;
            this.description = description;
        }

        // Getters and Setters
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

        public Double getExpense() {
            return expense;
        }

        public void setExpense(Double expense) {
            this.expense = expense;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // Constructors
    public ExpenseSummaryDTO() {
    }

    // Getters and Setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public Double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Double getAverageDailyExpense() {
        return averageDailyExpense;
    }

    public void setAverageDailyExpense(Double averageDailyExpense) {
        this.averageDailyExpense = averageDailyExpense;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public Integer getDaysWithExpenses() {
        return daysWithExpenses;
    }

    public void setDaysWithExpenses(Integer daysWithExpenses) {
        this.daysWithExpenses = daysWithExpenses;
    }

    public List<DailyExpenseDTO> getDailyBreakdown() {
        return dailyBreakdown;
    }

    public void setDailyBreakdown(List<DailyExpenseDTO> dailyBreakdown) {
        this.dailyBreakdown = dailyBreakdown;
    }
}
