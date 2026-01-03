-- =============================================
-- Expense Tracker Database Schema
-- Database: MySQL
-- =============================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS expense_tracker_db;
USE expense_tracker_db;

-- =============================================
-- Table: balance_entries
-- Stores daily balance entries for expense tracking
-- =============================================
DROP TABLE IF EXISTS balance_entries;

CREATE TABLE balance_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    balance DECIMAL(15, 2) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for better query performance
    INDEX idx_date (date),
    INDEX idx_created_at (created_at),
    
    -- Constraints
    CONSTRAINT chk_balance_positive CHECK (balance >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Optional: Create views for reporting
-- =============================================

-- View: Daily expenses with previous day comparison
CREATE OR REPLACE VIEW v_daily_expenses AS
SELECT 
    b.id,
    b.date,
    b.balance,
    b.description,
    LAG(b.balance) OVER (ORDER BY b.date) AS previous_balance,
    CASE 
        WHEN LAG(b.balance) OVER (ORDER BY b.date) IS NOT NULL 
        THEN GREATEST(0, LAG(b.balance) OVER (ORDER BY b.date) - b.balance)
        ELSE NULL 
    END AS daily_expense
FROM balance_entries b
ORDER BY b.date;

-- View: Monthly summary
CREATE OR REPLACE VIEW v_monthly_summary AS
SELECT 
    YEAR(date) AS year,
    MONTH(date) AS month,
    MONTHNAME(date) AS month_name,
    COUNT(*) AS total_entries,
    MIN(balance) AS min_balance,
    MAX(balance) AS max_balance,
    AVG(balance) AS avg_balance,
    FIRST_VALUE(balance) OVER (
        PARTITION BY YEAR(date), MONTH(date) 
        ORDER BY date
    ) AS opening_balance,
    LAST_VALUE(balance) OVER (
        PARTITION BY YEAR(date), MONTH(date) 
        ORDER BY date
        RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    ) AS closing_balance
FROM balance_entries
GROUP BY YEAR(date), MONTH(date), MONTHNAME(date), balance, date
ORDER BY year DESC, month DESC;

-- =============================================
-- Sample queries for reference
-- =============================================

-- Query: Get all entries with calculated expense
-- SELECT * FROM v_daily_expenses;

-- Query: Get entries for a specific month
-- SELECT * FROM balance_entries 
-- WHERE YEAR(date) = 2025 AND MONTH(date) = 1;

-- Query: Get monthly totals
-- SELECT 
--     YEAR(date) AS year,
--     MONTH(date) AS month,
--     SUM(
--         CASE 
--             WHEN LAG(balance) OVER (ORDER BY date) IS NOT NULL 
--             THEN GREATEST(0, LAG(balance) OVER (ORDER BY date) - balance)
--             ELSE 0 
--         END
--     ) AS total_expenses
-- FROM balance_entries
-- GROUP BY YEAR(date), MONTH(date);

-- =============================================
-- Stored Procedures (Optional)
-- =============================================

DELIMITER //

-- Procedure: Calculate daily expense for a specific date
CREATE PROCEDURE IF NOT EXISTS sp_calculate_daily_expense(IN p_date DATE)
BEGIN
    DECLARE v_current_balance DECIMAL(15, 2);
    DECLARE v_previous_balance DECIMAL(15, 2);
    DECLARE v_expense DECIMAL(15, 2);
    
    -- Get current date balance
    SELECT balance INTO v_current_balance 
    FROM balance_entries 
    WHERE date = p_date;
    
    -- Get previous date balance
    SELECT balance INTO v_previous_balance 
    FROM balance_entries 
    WHERE date = DATE_SUB(p_date, INTERVAL 1 DAY);
    
    -- Calculate expense
    IF v_current_balance IS NOT NULL AND v_previous_balance IS NOT NULL THEN
        SET v_expense = GREATEST(0, v_previous_balance - v_current_balance);
    ELSE
        SET v_expense = NULL;
    END IF;
    
    SELECT 
        p_date AS date,
        v_current_balance AS current_balance,
        v_previous_balance AS previous_balance,
        v_expense AS expense;
END //

-- Procedure: Get expense summary for date range
CREATE PROCEDURE IF NOT EXISTS sp_expense_summary(IN p_start_date DATE, IN p_end_date DATE)
BEGIN
    SELECT 
        COUNT(*) AS total_days,
        MIN(balance) AS min_balance,
        MAX(balance) AS max_balance,
        SUM(
            CASE 
                WHEN prev_balance IS NOT NULL 
                THEN GREATEST(0, prev_balance - balance)
                ELSE 0 
            END
        ) AS total_expenses
    FROM (
        SELECT 
            date,
            balance,
            LAG(balance) OVER (ORDER BY date) AS prev_balance
        FROM balance_entries
        WHERE date BETWEEN p_start_date AND p_end_date
    ) AS daily_data;
END //

DELIMITER ;

-- =============================================
-- Grant permissions (adjust as needed)
-- =============================================
-- GRANT ALL PRIVILEGES ON expense_tracker_db.* TO 'your_user'@'localhost';
-- FLUSH PRIVILEGES;
