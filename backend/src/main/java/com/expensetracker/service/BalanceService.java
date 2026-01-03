package com.expensetracker.service;

import com.expensetracker.dto.BalanceEntryDTO;
import com.expensetracker.dto.ExpenseSummaryDTO;
import com.expensetracker.dto.ExpenseSummaryDTO.DailyExpenseDTO;
import com.expensetracker.model.BalanceEntry;
import com.expensetracker.repository.BalanceEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for balance and expense operations
 * Contains business logic for calculating expenses and managing balance entries
 */
@Service
@Transactional
public class BalanceService {

    private final BalanceEntryRepository repository;

    @Autowired
    public BalanceService(BalanceEntryRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all balance entries
     */
    public List<BalanceEntryDTO> getAllEntries() {
        return repository.findAllByOrderByDateAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get entry by ID
     */
    public Optional<BalanceEntryDTO> getEntryById(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    /**
     * Get entry by date
     */
    public Optional<BalanceEntryDTO> getEntryByDate(LocalDate date) {
        return repository.findByDate(date).map(this::convertToDTO);
    }

    /**
     * Create a new balance entry
     */
    public BalanceEntryDTO createEntry(BalanceEntryDTO dto) {
        // Check if entry already exists for this date
        if (repository.existsByDate(dto.getDate())) {
            throw new IllegalArgumentException("An entry already exists for this date. Use update instead.");
        }

        BalanceEntry entry = new BalanceEntry();
        entry.setDate(dto.getDate());
        entry.setBalance(dto.getBalance());
        entry.setDescription(dto.getDescription());

        BalanceEntry savedEntry = repository.save(entry);
        return convertToDTO(savedEntry);
    }

    /**
     * Update an existing balance entry
     */
    public BalanceEntryDTO updateEntry(Long id, BalanceEntryDTO dto) {
        BalanceEntry entry = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found with id: " + id));

        // If date is changing, check for conflicts
        if (!entry.getDate().equals(dto.getDate()) && repository.existsByDate(dto.getDate())) {
            throw new IllegalArgumentException("An entry already exists for the new date.");
        }

        entry.setDate(dto.getDate());
        entry.setBalance(dto.getBalance());
        entry.setDescription(dto.getDescription());

        BalanceEntry updatedEntry = repository.save(entry);
        return convertToDTO(updatedEntry);
    }

    /**
     * Delete an entry by ID
     */
    public void deleteEntry(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Entry not found with id: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Delete all entries
     */
    public void deleteAllEntries() {
        repository.deleteAll();
    }

    /**
     * Get entries for a date range
     */
    public List<BalanceEntryDTO> getEntriesByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get entries for a specific month
     */
    public List<BalanceEntryDTO> getEntriesByMonth(int year, int month) {
        return repository.findByYearAndMonth(year, month)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calculate expense summary for a date range
     */
    public ExpenseSummaryDTO getExpenseSummary(LocalDate startDate, LocalDate endDate) {
        List<BalanceEntry> entries = repository.findByDateBetween(startDate, endDate);

        ExpenseSummaryDTO summary = new ExpenseSummaryDTO();
        summary.setStartDate(startDate);
        summary.setEndDate(endDate);
        summary.setTotalDays(entries.size());

        if (entries.isEmpty()) {
            summary.setOpeningBalance(0.0);
            summary.setClosingBalance(0.0);
            summary.setTotalExpenses(0.0);
            summary.setAverageDailyExpense(0.0);
            summary.setDaysWithExpenses(0);
            summary.setDailyBreakdown(new ArrayList<>());
            return summary;
        }

        // Opening and closing balances
        summary.setOpeningBalance(entries.get(0).getBalance());
        summary.setClosingBalance(entries.get(entries.size() - 1).getBalance());

        // Calculate daily expenses
        List<DailyExpenseDTO> dailyBreakdown = new ArrayList<>();
        double totalExpenses = 0.0;
        int daysWithExpenses = 0;

        for (int i = 0; i < entries.size(); i++) {
            BalanceEntry current = entries.get(i);
            Double expense = calculateDailyExpense(current.getDate());

            DailyExpenseDTO daily = new DailyExpenseDTO();
            daily.setDate(current.getDate());
            daily.setBalance(current.getBalance());
            daily.setExpense(expense);
            daily.setDescription(current.getDescription());

            if (expense != null && expense > 0) {
                totalExpenses += expense;
                daysWithExpenses++;
            }

            dailyBreakdown.add(daily);
        }

        summary.setTotalExpenses(totalExpenses);
        summary.setDaysWithExpenses(daysWithExpenses);
        summary.setAverageDailyExpense(daysWithExpenses > 0 ? totalExpenses / daysWithExpenses : 0.0);
        summary.setDailyBreakdown(dailyBreakdown);

        return summary;
    }

    /**
     * Calculate monthly summary
     */
    public ExpenseSummaryDTO getMonthlySummary(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return getExpenseSummary(startDate, endDate);
    }

    /**
     * Calculate daily expense for a specific date
     * Expense = Previous day balance - Current day balance
     */
    public Double calculateDailyExpense(LocalDate date) {
        Optional<BalanceEntry> currentEntry = repository.findByDate(date);
        Optional<BalanceEntry> previousEntry = repository.findByDate(date.minusDays(1));

        if (currentEntry.isEmpty() || previousEntry.isEmpty()) {
            return null;
        }

        double expense = previousEntry.get().getBalance() - currentEntry.get().getBalance();
        return expense > 0 ? expense : 0.0;
    }

    /**
     * Convert entity to DTO with calculated expense
     */
    private BalanceEntryDTO convertToDTO(BalanceEntry entry) {
        BalanceEntryDTO dto = new BalanceEntryDTO();
        dto.setId(entry.getId());
        dto.setDate(entry.getDate());
        dto.setBalance(entry.getBalance());
        dto.setDescription(entry.getDescription());
        dto.setDailyExpense(calculateDailyExpense(entry.getDate()));
        return dto;
    }
}
