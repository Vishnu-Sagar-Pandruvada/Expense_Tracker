package com.expensetracker.repository;

import com.expensetracker.model.BalanceEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BalanceEntry entity
 * Provides CRUD operations and custom queries for balance data
 */
@Repository
public interface BalanceEntryRepository extends JpaRepository<BalanceEntry, Long> {

    /**
     * Find a balance entry by date
     */
    Optional<BalanceEntry> findByDate(LocalDate date);

    /**
     * Check if an entry exists for a specific date
     */
    boolean existsByDate(LocalDate date);

    /**
     * Find all entries between two dates (inclusive)
     */
    @Query("SELECT b FROM BalanceEntry b WHERE b.date BETWEEN :startDate AND :endDate ORDER BY b.date ASC")
    List<BalanceEntry> findByDateBetween(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);

    /**
     * Find all entries for a specific month
     */
    @Query("SELECT b FROM BalanceEntry b WHERE YEAR(b.date) = :year AND MONTH(b.date) = :month ORDER BY b.date ASC")
    List<BalanceEntry> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Find the entry for the previous day
     */
    @Query("SELECT b FROM BalanceEntry b WHERE b.date = :date")
    Optional<BalanceEntry> findPreviousDayEntry(@Param("date") LocalDate date);

    /**
     * Find all entries ordered by date descending
     */
    List<BalanceEntry> findAllByOrderByDateDesc();

    /**
     * Find all entries ordered by date ascending
     */
    List<BalanceEntry> findAllByOrderByDateAsc();

    /**
     * Get the most recent entry
     */
    Optional<BalanceEntry> findTopByOrderByDateDesc();

    /**
     * Get the oldest entry
     */
    Optional<BalanceEntry> findTopByOrderByDateAsc();

    /**
     * Count entries in a date range
     */
    @Query("SELECT COUNT(b) FROM BalanceEntry b WHERE b.date BETWEEN :startDate AND :endDate")
    Long countByDateBetween(@Param("startDate") LocalDate startDate, 
                            @Param("endDate") LocalDate endDate);
}
