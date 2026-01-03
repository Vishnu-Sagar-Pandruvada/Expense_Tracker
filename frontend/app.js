// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// State Management
let currentMonth = new Date().getMonth();
let currentYear = new Date().getFullYear();
let balanceEntries = [];

// DOM Elements
const tabBtns = document.querySelectorAll('.tab-btn');
const tabPanels = document.querySelectorAll('.tab-panel');
const balanceForm = document.getElementById('balanceForm');
const entryDate = document.getElementById('entryDate');
const balanceAmount = document.getElementById('balanceAmount');
const description = document.getElementById('description');

// Initialize App
document.addEventListener('DOMContentLoaded', () => {
    initializeApp();
});

async function initializeApp() {
    // Set default date to today
    const today = new Date().toISOString().split('T')[0];
    entryDate.value = today;
    document.getElementById('dailyStartDate').value = getFirstDayOfMonth();
    document.getElementById('dailyEndDate').value = today;
    document.getElementById('monthSelect').value = today.substring(0, 7);

    // Initialize tabs
    initializeTabs();
    
    // Initialize calendar
    renderCalendar();
    
    // Initialize event listeners
    initializeEventListeners();
    
    // Check backend connection and load data
    await checkBackendConnection();
    await loadAllEntries();
}

function initializeTabs() {
    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabId = btn.dataset.tab;
            
            // Update active tab button
            tabBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            
            // Update active tab panel
            tabPanels.forEach(panel => panel.classList.remove('active'));
            document.getElementById(tabId).classList.add('active');
            
            // Refresh data for specific tabs
            if (tabId === 'preview') {
                refreshPreview();
            } else if (tabId === 'calendar') {
                renderCalendar();
            }
        });
    });
}

function initializeEventListeners() {
    // Balance Form Submit
    balanceForm.addEventListener('submit', handleBalanceSubmit);
    
    // Calendar Navigation
    document.getElementById('prevMonth').addEventListener('click', () => {
        currentMonth--;
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        }
        renderCalendar();
    });
    
    document.getElementById('nextMonth').addEventListener('click', () => {
        currentMonth++;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }
        renderCalendar();
    });
    
    // Daily Filter
    document.getElementById('filterDaily').addEventListener('click', filterDailyView);
    
    // Monthly Filter
    document.getElementById('filterMonthly').addEventListener('click', filterMonthlyView);
    
    // Test Connection
    document.getElementById('testConnection').addEventListener('click', testBackendConnection);
    
    // Clear All Data
    document.getElementById('clearAllData').addEventListener('click', clearAllData);
}

// API Functions
async function checkBackendConnection() {
    const statusDot = document.querySelector('.status-dot');
    const statusText = document.querySelector('.status-text');
    
    try {
        const response = await fetch(`${API_BASE_URL}/health`);
        if (response.ok) {
            statusDot.className = 'status-dot connected';
            statusText.textContent = 'Connected to Backend';
            return true;
        }
    } catch (error) {
        console.log('Backend not available, using local storage');
    }
    
    statusDot.className = 'status-dot disconnected';
    statusText.textContent = 'Offline Mode (Local Storage)';
    return false;
}

async function loadAllEntries() {
    try {
        const isConnected = await checkBackendConnection();
        
        if (isConnected) {
            const response = await fetch(`${API_BASE_URL}/balances`);
            if (response.ok) {
                balanceEntries = await response.json();
            }
        } else {
            // Load from local storage
            const stored = localStorage.getItem('balanceEntries');
            balanceEntries = stored ? JSON.parse(stored) : [];
        }
        
        // Sort by date
        balanceEntries.sort((a, b) => new Date(a.date) - new Date(b.date));
        
        updateRecentEntries();
        renderCalendar();
    } catch (error) {
        console.error('Error loading entries:', error);
        showToast('Error loading data', 'error');
    }
}

async function handleBalanceSubmit(e) {
    e.preventDefault();
    
    const entry = {
        date: entryDate.value,
        balance: parseFloat(balanceAmount.value),
        description: description.value || 'Daily balance'
    };
    
    try {
        const isConnected = await checkBackendConnection();
        
        if (isConnected) {
            // Check if entry exists for this date
            const existingEntry = balanceEntries.find(e => e.date === entry.date);
            
            if (existingEntry) {
                // Update existing entry
                const response = await fetch(`${API_BASE_URL}/balances/${existingEntry.id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(entry)
                });
                
                if (response.ok) {
                    const updatedEntry = await response.json();
                    const index = balanceEntries.findIndex(e => e.id === existingEntry.id);
                    balanceEntries[index] = updatedEntry;
                    showToast('Balance updated successfully!', 'success');
                }
            } else {
                // Create new entry
                const response = await fetch(`${API_BASE_URL}/balances`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(entry)
                });
                
                if (response.ok) {
                    const newEntry = await response.json();
                    balanceEntries.push(newEntry);
                    showToast('Balance added successfully!', 'success');
                }
            }
        } else {
            // Local storage mode
            const existingIndex = balanceEntries.findIndex(e => e.date === entry.date);
            
            if (existingIndex !== -1) {
                balanceEntries[existingIndex] = { ...balanceEntries[existingIndex], ...entry };
                showToast('Balance updated (offline mode)', 'warning');
            } else {
                entry.id = Date.now();
                balanceEntries.push(entry);
                showToast('Balance added (offline mode)', 'warning');
            }
            
            localStorage.setItem('balanceEntries', JSON.stringify(balanceEntries));
        }
        
        // Sort and refresh
        balanceEntries.sort((a, b) => new Date(a.date) - new Date(b.date));
        updateRecentEntries();
        renderCalendar();
        
        // Reset form
        balanceAmount.value = '';
        description.value = '';
        
    } catch (error) {
        console.error('Error saving entry:', error);
        showToast('Error saving entry', 'error');
    }
}

async function deleteEntry(id) {
    if (!confirm('Are you sure you want to delete this entry?')) return;
    
    try {
        const isConnected = await checkBackendConnection();
        
        if (isConnected) {
            const response = await fetch(`${API_BASE_URL}/balances/${id}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                balanceEntries = balanceEntries.filter(e => e.id !== id);
                showToast('Entry deleted successfully!', 'success');
            }
        } else {
            balanceEntries = balanceEntries.filter(e => e.id !== id);
            localStorage.setItem('balanceEntries', JSON.stringify(balanceEntries));
            showToast('Entry deleted (offline mode)', 'warning');
        }
        
        updateRecentEntries();
        renderCalendar();
        
    } catch (error) {
        console.error('Error deleting entry:', error);
        showToast('Error deleting entry', 'error');
    }
}

// UI Update Functions
function updateRecentEntries() {
    const container = document.getElementById('recentEntries');
    
    if (balanceEntries.length === 0) {
        container.innerHTML = '<p class="no-data">No entries yet. Add your first balance entry above.</p>';
        return;
    }
    
    // Get last 10 entries (most recent first)
    const recentEntries = [...balanceEntries].reverse().slice(0, 10);
    
    container.innerHTML = recentEntries.map(entry => {
        const expense = calculateExpenseForDate(entry.date);
        const expenseText = expense !== null ? `<span class="entry-expense">Spent: ₹${expense.toFixed(2)}</span>` : '';
        
        return `
            <div class="entry-item">
                <div class="entry-info">
                    <div class="entry-date">${formatDate(entry.date)} ${getDayIndicator(entry.date)}</div>
                    <div class="entry-description">${entry.description || ''} ${expenseText}</div>
                </div>
                <div class="entry-amount">₹${entry.balance.toFixed(2)}</div>
                <div class="entry-actions">
                    <button class="btn btn-danger" onclick="deleteEntry(${entry.id})">🗑️</button>
                </div>
            </div>
        `;
    }).join('');
}

function getDayIndicator(dateStr) {
    const entryDate = new Date(dateStr);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    entryDate.setHours(0, 0, 0, 0);
    
    const diffDays = Math.round((entryDate - today) / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) return '<span style="color: #10b981; font-weight: 600;">(Today)</span>';
    if (diffDays === -1) return '<span style="color: #f59e0b; font-weight: 600;">(Yesterday)</span>';
    if (diffDays === 1) return '<span style="color: #3b82f6; font-weight: 600;">(Tomorrow)</span>';
    if (diffDays < 0) return `<span style="color: #6b7280;">(${Math.abs(diffDays)} days ago)</span>`;
    return `<span style="color: #6b7280;">(${diffDays} days from now)</span>`;
}

function calculateExpenseForDate(dateStr) {
    const currentEntry = balanceEntries.find(e => e.date === dateStr);
    if (!currentEntry) return null;
    
    // Find previous day's entry
    const currentDate = new Date(dateStr);
    const prevDate = new Date(currentDate);
    prevDate.setDate(prevDate.getDate() - 1);
    const prevDateStr = prevDate.toISOString().split('T')[0];
    
    const prevEntry = balanceEntries.find(e => e.date === prevDateStr);
    
    if (!prevEntry) return null;
    
    // Expense = Previous balance - Current balance (if positive, money was spent)
    const expense = prevEntry.balance - currentEntry.balance;
    return expense > 0 ? expense : 0;
}

function renderCalendar() {
    const grid = document.getElementById('calendarGrid');
    const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                        'July', 'August', 'September', 'October', 'November', 'December'];
    
    document.getElementById('currentMonth').textContent = `${monthNames[currentMonth]} ${currentYear}`;
    
    // Day headers
    const dayHeaders = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    let html = dayHeaders.map(day => `<div class="calendar-day-header">${day}</div>`).join('');
    
    // Get first day of month and total days
    const firstDay = new Date(currentYear, currentMonth, 1).getDay();
    const totalDays = new Date(currentYear, currentMonth + 1, 0).getDate();
    const today = new Date();
    
    // Empty cells for days before first day
    for (let i = 0; i < firstDay; i++) {
        html += '<div class="calendar-day empty"></div>';
    }
    
    // Days of the month
    for (let day = 1; day <= totalDays; day++) {
        const dateStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        const entry = balanceEntries.find(e => e.date === dateStr);
        const expense = calculateExpenseForDate(dateStr);
        
        let classes = 'calendar-day';
        let amountText = '';
        
        if (entry) {
            classes += expense !== null && expense > 0 ? ' has-expense' : ' has-data';
            amountText = `<div class="day-amount">₹${entry.balance.toFixed(0)}</div>`;
        }
        
        if (day === today.getDate() && currentMonth === today.getMonth() && currentYear === today.getFullYear()) {
            classes += ' today';
        }
        
        html += `
            <div class="${classes}" onclick="showDayDetails('${dateStr}')">
                <span class="day-number">${day}</span>
                ${amountText}
            </div>
        `;
    }
    
    grid.innerHTML = html;
}

function showDayDetails(dateStr) {
    const card = document.getElementById('selectedDayCard');
    const title = document.getElementById('selectedDayTitle');
    const details = document.getElementById('selectedDayDetails');
    
    const entry = balanceEntries.find(e => e.date === dateStr);
    const expense = calculateExpenseForDate(dateStr);
    
    title.textContent = `Details for ${formatDate(dateStr)} ${getDayIndicator(dateStr)}`;
    
    if (entry) {
        let html = `
            <div class="entry-item">
                <div class="entry-info">
                    <div class="entry-date">Balance</div>
                    <div class="entry-description">${entry.description || 'No description'}</div>
                </div>
                <div class="entry-amount">₹${entry.balance.toFixed(2)}</div>
            </div>
        `;
        
        if (expense !== null) {
            html += `
                <div class="entry-item">
                    <div class="entry-info">
                        <div class="entry-date">Daily Expense</div>
                        <div class="entry-description">Calculated from previous day</div>
                    </div>
                    <div class="entry-amount entry-expense">₹${expense.toFixed(2)}</div>
                </div>
            `;
        } else {
            html += `<p class="no-data">No previous day entry to calculate expense.</p>`;
        }
        
        details.innerHTML = html;
    } else {
        details.innerHTML = `
            <p class="no-data">No entry for this date.</p>
            <button class="btn btn-primary" onclick="quickAddEntry('${dateStr}')">Add Entry for This Date</button>
        `;
    }
    
    card.style.display = 'block';
}

function quickAddEntry(dateStr) {
    entryDate.value = dateStr;
    document.querySelector('[data-tab="entry"]').click();
    balanceAmount.focus();
}

function filterDailyView() {
    const startDate = document.getElementById('dailyStartDate').value;
    const endDate = document.getElementById('dailyEndDate').value;
    
    if (!startDate || !endDate) {
        showToast('Please select both start and end dates', 'error');
        return;
    }
    
    const filteredEntries = balanceEntries.filter(e => e.date >= startDate && e.date <= endDate);
    
    if (filteredEntries.length === 0) {
        document.getElementById('dailyBreakdown').innerHTML = '<p class="no-data">No entries found for the selected date range.</p>';
        document.getElementById('totalDays').textContent = '0';
        document.getElementById('totalExpenses').textContent = '₹0';
        document.getElementById('avgExpense').textContent = '₹0';
        return;
    }
    
    // Calculate statistics
    let totalExpenses = 0;
    let expenseDays = 0;
    
    const breakdownHtml = filteredEntries.map(entry => {
        const expense = calculateExpenseForDate(entry.date);
        if (expense !== null && expense > 0) {
            totalExpenses += expense;
            expenseDays++;
        }
        
        return `
            <div class="breakdown-item">
                <div class="breakdown-date">${formatDate(entry.date)} ${getDayIndicator(entry.date)}</div>
                <div class="breakdown-balance">₹${entry.balance.toFixed(2)}</div>
                <div class="breakdown-expense">${expense !== null ? '₹' + expense.toFixed(2) : 'N/A'}</div>
                <div class="breakdown-note">${entry.description || '-'}</div>
            </div>
        `;
    }).join('');
    
    document.getElementById('dailyBreakdown').innerHTML = `
        <div class="breakdown-item header">
            <div>Date</div>
            <div>Balance</div>
            <div>Expense</div>
            <div>Note</div>
        </div>
        ${breakdownHtml}
    `;
    
    document.getElementById('totalDays').textContent = filteredEntries.length;
    document.getElementById('totalExpenses').textContent = `₹${totalExpenses.toFixed(2)}`;
    document.getElementById('avgExpense').textContent = expenseDays > 0 ? `₹${(totalExpenses / expenseDays).toFixed(2)}` : '₹0';
}

function filterMonthlyView() {
    const monthStr = document.getElementById('monthSelect').value;
    
    if (!monthStr) {
        showToast('Please select a month', 'error');
        return;
    }
    
    const [year, month] = monthStr.split('-').map(Number);
    const firstDay = `${year}-${String(month).padStart(2, '0')}-01`;
    const lastDay = `${year}-${String(month).padStart(2, '0')}-${new Date(year, month, 0).getDate()}`;
    
    const monthlyEntries = balanceEntries.filter(e => e.date >= firstDay && e.date <= lastDay);
    
    const report = document.getElementById('monthlyReport');
    const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                        'July', 'August', 'September', 'October', 'November', 'December'];
    
    document.getElementById('monthlyReportTitle').textContent = `${monthNames[month - 1]} ${year} Report`;
    
    if (monthlyEntries.length === 0) {
        document.getElementById('monthOpening').textContent = '₹0';
        document.getElementById('monthClosing').textContent = '₹0';
        document.getElementById('monthTotal').textContent = '₹0';
        document.getElementById('monthlyBreakdown').innerHTML = '<p class="no-data">No entries for this month.</p>';
        report.style.display = 'block';
        return;
    }
    
    // Sort by date
    monthlyEntries.sort((a, b) => new Date(a.date) - new Date(b.date));
    
    const openingBalance = monthlyEntries[0].balance;
    const closingBalance = monthlyEntries[monthlyEntries.length - 1].balance;
    
    let totalSpent = 0;
    monthlyEntries.forEach(entry => {
        const expense = calculateExpenseForDate(entry.date);
        if (expense !== null && expense > 0) {
            totalSpent += expense;
        }
    });
    
    document.getElementById('monthOpening').textContent = `₹${openingBalance.toFixed(2)}`;
    document.getElementById('monthClosing').textContent = `₹${closingBalance.toFixed(2)}`;
    document.getElementById('monthTotal').textContent = `₹${totalSpent.toFixed(2)}`;
    
    const breakdownHtml = monthlyEntries.map(entry => {
        const expense = calculateExpenseForDate(entry.date);
        return `
            <div class="breakdown-item">
                <div class="breakdown-date">${formatDate(entry.date)}</div>
                <div class="breakdown-balance">₹${entry.balance.toFixed(2)}</div>
                <div class="breakdown-expense">${expense !== null ? '₹' + expense.toFixed(2) : 'N/A'}</div>
                <div class="breakdown-note">${entry.description || '-'}</div>
            </div>
        `;
    }).join('');
    
    document.getElementById('monthlyBreakdown').innerHTML = `
        <div class="breakdown-item header">
            <div>Date</div>
            <div>Balance</div>
            <div>Expense</div>
            <div>Note</div>
        </div>
        ${breakdownHtml}
    `;
    
    report.style.display = 'block';
}

function refreshPreview() {
    // All Entries Preview
    const allEntriesContainer = document.getElementById('allEntriesPreview');
    
    if (balanceEntries.length === 0) {
        allEntriesContainer.innerHTML = '<p class="no-data">No data available. Start by adding entries.</p>';
    } else {
        const sortedEntries = [...balanceEntries].sort((a, b) => new Date(b.date) - new Date(a.date));
        allEntriesContainer.innerHTML = sortedEntries.map(entry => `
            <div class="entry-item">
                <div class="entry-info">
                    <div class="entry-date">${formatDate(entry.date)} ${getDayIndicator(entry.date)}</div>
                    <div class="entry-description">${entry.description || 'No description'}</div>
                </div>
                <div class="entry-amount">₹${entry.balance.toFixed(2)}</div>
            </div>
        `).join('');
    }
    
    // Calculated Expenses Preview
    const expensesContainer = document.getElementById('calculatedExpensesPreview');
    
    const expenseData = balanceEntries.map(entry => {
        const expense = calculateExpenseForDate(entry.date);
        return { date: entry.date, expense };
    }).filter(e => e.expense !== null && e.expense > 0);
    
    if (expenseData.length === 0) {
        expensesContainer.innerHTML = '<p class="no-data">Add at least 2 consecutive day entries to see expense calculations.</p>';
    } else {
        expensesContainer.innerHTML = expenseData.map(data => `
            <div class="entry-item">
                <div class="entry-info">
                    <div class="entry-date">${formatDate(data.date)} ${getDayIndicator(data.date)}</div>
                    <div class="entry-description">Daily Expense</div>
                </div>
                <div class="entry-amount entry-expense">₹${data.expense.toFixed(2)}</div>
            </div>
        `).join('');
    }
}

async function testBackendConnection() {
    const resultDiv = document.getElementById('apiTestResult');
    resultDiv.className = 'api-result';
    resultDiv.textContent = 'Testing connection...';
    resultDiv.style.display = 'block';
    
    try {
        const response = await fetch(`${API_BASE_URL}/health`);
        
        if (response.ok) {
            const data = await response.json();
            resultDiv.className = 'api-result success';
            resultDiv.innerHTML = `
                ✅ <strong>Connection Successful!</strong><br>
                Status: ${data.status}<br>
                Message: ${data.message}<br>
                Database: ${data.database || 'Connected'}
            `;
        } else {
            throw new Error('Server returned an error');
        }
    } catch (error) {
        resultDiv.className = 'api-result error';
        resultDiv.innerHTML = `
            ❌ <strong>Connection Failed</strong><br>
            Error: ${error.message}<br>
            <br>
            Make sure the Spring Boot backend is running on port 8080.<br>
            The app is currently using local storage for data persistence.
        `;
    }
}

async function clearAllData() {
    if (!confirm('Are you sure you want to delete ALL data? This cannot be undone!')) return;
    
    try {
        const isConnected = await checkBackendConnection();
        
        if (isConnected) {
            const response = await fetch(`${API_BASE_URL}/balances/clear`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                balanceEntries = [];
                showToast('All data cleared successfully!', 'success');
            }
        } else {
            localStorage.removeItem('balanceEntries');
            balanceEntries = [];
            showToast('All local data cleared!', 'warning');
        }
        
        updateRecentEntries();
        renderCalendar();
        refreshPreview();
        
    } catch (error) {
        console.error('Error clearing data:', error);
        showToast('Error clearing data', 'error');
    }
}

// Utility Functions
function formatDate(dateStr) {
    const options = { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateStr).toLocaleDateString('en-US', options);
}

function getFirstDayOfMonth() {
    const date = new Date();
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-01`;
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Make deleteEntry available globally
window.deleteEntry = deleteEntry;
window.showDayDetails = showDayDetails;
window.quickAddEntry = quickAddEntry;
