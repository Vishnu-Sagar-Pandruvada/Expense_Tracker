# 💰 Daily Expense Tracker

A full-stack web application for tracking daily expenses by recording daily balances. The application automatically calculates expenses based on the difference between consecutive day balances.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Demo](#-demo)
- [How It Works](#-how-it-works)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
- [API Documentation](#-api-documentation)
- [Usage Guide](#-usage-guide)
- [Screenshots](#-screenshots)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

**Daily Expense Tracker** is a simple yet powerful application designed to help you track your daily expenses without the hassle of manually entering each expense. Instead of logging individual transactions, you simply record your daily balance, and the app automatically calculates how much you spent each day.

### The Problem It Solves

Traditional expense trackers require you to:
- Remember every single purchase
- Manually categorize each expense
- Enter data multiple times a day

### Our Solution

With Daily Expense Tracker:
- Record your balance once a day (takes 10 seconds!)
- App automatically calculates daily expenses
- View trends over days, weeks, and months
- Works offline with local storage backup

---

## ✨ Features

### 📝 Balance Entry
- **Quick Entry**: Add your daily balance in seconds
- **Date Selection**: Enter balances for any date (past or future)
- **Description Support**: Add optional notes to each entry
- **Smart Detection**: Automatically identifies if entry is for today, yesterday, or another day
- **Duplicate Prevention**: Warns if an entry already exists for a date

### 📅 Interactive Calendar
- **Visual Overview**: See your entire month at a glance
- **Color-Coded Days**: 
  - 🔵 Blue: Days with balance data
  - 🔴 Red: Days with calculated expenses
  - 🟣 Purple: Today's date
- **Click for Details**: Click any day to view detailed information
- **Month Navigation**: Easy navigation between months

### 📊 Daily View
- **Date Range Filter**: Analyze expenses for any custom period
- **Summary Statistics**:
  - Total days tracked
  - Total expenses
  - Average daily expense
- **Detailed Breakdown**: See balance and expense for each day
- **Smart Day Labels**: Shows "Today", "Yesterday", "X days ago"

### 📈 Monthly View
- **Monthly Reports**: Complete monthly expense summary
- **Key Metrics**:
  - Opening balance (first day of month)
  - Closing balance (last day of month)
  - Total monthly spending
- **Day-by-Day Breakdown**: Detailed view of entire month

### 🔍 Preview & Test
- **Live Data Preview**: See all your entries in real-time
- **Calculated Expenses View**: Review all expense calculations
- **API Connection Test**: Verify backend connectivity
- **Data Management**: Option to clear all data

### 🔄 Offline Support
- **Local Storage Backup**: Works without internet
- **Auto-Sync**: Syncs with database when connection restored
- **Seamless Experience**: No data loss during offline periods

### 🛡️ Data Validation
- **Input Validation**: Ensures valid balance amounts
- **Date Validation**: Prevents invalid date entries
- **Error Handling**: Clear error messages for any issues

---

## 🎮 Demo

### Live Testing
1. Open `index.html` in your browser (works offline!)
2. Add today's balance: ₹5000
3. Add yesterday's balance: ₹5500
4. See calculated expense: ₹500

---

## 🔍 How It Works

### Expense Calculation Logic

```
Daily Expense = Previous Day's Balance - Current Day's Balance
```

### Example

| Date | Balance | Calculation | Expense |
|------|---------|-------------|---------|
| Jan 14 | ₹5,500 | - | - |
| Jan 15 | ₹5,000 | ₹5,500 - ₹5,000 | ₹500 |
| Jan 16 | ₹4,200 | ₹5,000 - ₹4,200 | ₹800 |
| Jan 17 | ₹4,500 | Balance increased | ₹0 |

**Note**: If balance increases (you added money), expense is shown as ₹0.

---

## 🛠️ Tech Stack

### Frontend
| Technology | Purpose |
|------------|---------|
| HTML5 | Structure |
| CSS3 | Styling & Animations |
| JavaScript (ES6+) | Application Logic |
| Google Fonts (Inter) | Typography |

### Backend
| Technology | Purpose |
|------------|---------|
| Java 17 | Programming Language |
| Spring Boot 3.2 | Application Framework |
| Spring Data JPA | Database ORM |
| Hibernate | JPA Implementation |
| Maven | Dependency Management |

### Database
| Technology | Purpose |
|------------|---------|
| MySQL 8.0 | Primary Database |
| H2 Database | Testing/Development |

### Tools
| Tool | Purpose |
|------|---------|
| Eclipse IDE | Development |
| Postman | API Testing |
| Git | Version Control |

---

## 📁 Project Structure

```
expense-tracker/
│
├── 📂 frontend/                    # Frontend Application
│   ├── index.html                 # Main HTML file
│   ├── styles.css                 # CSS styles
│   └── app.js                     # JavaScript logic
│
├── 📂 backend/                     # Spring Boot Backend
│   ├── pom.xml                    # Maven dependencies
│   └── src/main/
│       ├── java/com/expensetracker/
│       │   ├── ExpenseTrackerApplication.java    # Main class
│       │   ├── 📂 model/
│       │   │   └── BalanceEntry.java             # Entity
│       │   ├── 📂 dto/
│       │   │   ├── BalanceEntryDTO.java          # Data transfer
│       │   │   └── ExpenseSummaryDTO.java        # Summary data
│       │   ├── 📂 repository/
│       │   │   └── BalanceEntryRepository.java   # Data access
│       │   ├── 📂 service/
│       │   │   └── BalanceService.java           # Business logic
│       │   ├── 📂 controller/
│       │   │   ├── BalanceController.java        # REST APIs
│       │   │   └── HealthController.java         # Health check
│       │   ├── 📂 config/
│       │   │   └── WebConfig.java                # CORS config
│       │   └── 📂 exception/
│       │       └── GlobalExceptionHandler.java   # Error handling
│       └── resources/
│           └── application.properties            # App config
│
├── 📂 database/
│   └── schema.sql                 # Database schema
│
├── index.html                     # Standalone version (all-in-one)
└── README.md                      # This file
```

---

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

| Software | Version | Download |
|----------|---------|----------|
| Java JDK | 17 or higher | [Download](https://adoptium.net/) |
| Maven | 3.6+ | [Download](https://maven.apache.org/download.cgi) |
| MySQL | 8.0+ | [Download](https://dev.mysql.com/downloads/mysql/) |
| Eclipse IDE | Latest | [Download](https://www.eclipse.org/downloads/) |

### Installation

#### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/expense-tracker.git
cd expense-tracker
```

#### Step 2: Create MySQL Database

```sql
mysql -u root -p
```

```sql
CREATE DATABASE expense_tracker_db;
EXIT;
```

#### Step 3: Configure Database Connection

Open `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD_HERE
```

#### Step 4: Import Backend in Eclipse

1. Open Eclipse IDE
2. `File` → `Import` → `Maven` → `Existing Maven Projects`
3. Browse to `backend` folder
4. Select `pom.xml`
5. Click `Finish`
6. Wait for dependencies to download

#### Step 5: Build the Project

```bash
cd backend
mvn clean install
```

### Running the Application

#### Start Backend

**Option 1: Using Eclipse**
1. Right-click `ExpenseTrackerApplication.java`
2. Select `Run As` → `Java Application`

**Option 2: Using Command Line**
```bash
cd backend
mvn spring-boot:run
```

#### Start Frontend

1. Navigate to `frontend` folder
2. Open `index.html` in your browser

**Or use the standalone version:**
- Open `index.html` (root folder) directly in browser

#### Verify Installation

Open browser and navigate to:
```
http://localhost:8080/api/health
```

Expected response:
```json
{
  "status": "UP",
  "message": "Expense Tracker API is running",
  "database": "Connected",
  "databaseProduct": "MySQL"
}
```

---

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

#### Health Check
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Check API and database status |
| GET | `/info` | Get API information |

#### Balance Entries
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/balances` | Get all balance entries |
| GET | `/balances/{id}` | Get entry by ID |
| GET | `/balances/date/{date}` | Get entry by date |
| POST | `/balances` | Create new entry |
| PUT | `/balances/{id}` | Update existing entry |
| DELETE | `/balances/{id}` | Delete entry |
| DELETE | `/balances/clear` | Delete all entries |

#### Analysis & Reports
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/balances/range?startDate=&endDate=` | Get entries in date range |
| GET | `/balances/month?year=&month=` | Get entries for month |
| GET | `/balances/summary?startDate=&endDate=` | Get expense summary |
| GET | `/balances/monthly-summary?year=&month=` | Get monthly summary |
| GET | `/balances/expense/{date}` | Calculate expense for date |

### Request/Response Examples

#### Create Entry
**Request:**
```http
POST /api/balances
Content-Type: application/json

{
  "date": "2025-01-15",
  "balance": 5000.00,
  "description": "End of day balance"
}
```

**Response:**
```json
{
  "id": 1,
  "date": "2025-01-15",
  "balance": 5000.00,
  "description": "End of day balance",
  "dailyExpense": null
}
```

#### Get Monthly Summary
**Request:**
```http
GET /api/balances/monthly-summary?year=2025&month=1
```

**Response:**
```json
{
  "startDate": "2025-01-01",
  "endDate": "2025-01-31",
  "openingBalance": 10000.00,
  "closingBalance": 7500.00,
  "totalExpenses": 2500.00,
  "averageDailyExpense": 250.00,
  "totalDays": 15,
  "daysWithExpenses": 10,
  "dailyBreakdown": [...]
}
```

---

## 📖 Usage Guide

### Adding Your First Entry

1. **Open the App**: Launch `index.html` in your browser
2. **Go to Entry Tab**: Click "📝 Entry" tab
3. **Select Date**: Choose today's date
4. **Enter Balance**: Type your current balance (e.g., 5000)
5. **Add Description** (Optional): Add a note
6. **Submit**: Click "Add Balance Entry"

### Tracking Daily Expenses

For accurate expense tracking:

1. **Record balance at the same time each day** (e.g., end of day)
2. **Be consistent** with which balance you track (wallet, bank, total)
3. **Add entries for consecutive days** for expense calculations

### Understanding the Calendar

| Color | Meaning |
|-------|---------|
| 🔵 Blue border | Has balance data, no expense calculated |
| 🔴 Red border | Has expense calculated |
| 🟣 Purple fill | Today's date |
| ⬜ No border | No data for this day |

### Viewing Reports

1. **Daily View**: Select date range → Click "Apply Filter"
2. **Monthly View**: Select month → Click "View Monthly Report"

---


## 📋 Roadmap

- [ ] User authentication
- [ ] Multiple currency support
- [ ] Expense categories
- [ ] Data export (CSV, PDF)
- [ ] Dark mode
- [ ] Mobile app (React Native)
- [ ] Budget alerts
- [ ] Recurring expenses

---


## 🐛 Troubleshooting

### Backend won't start

**Port 8080 in use:**
```properties
# In application.properties
server.port=8081
```

**MySQL connection failed:**
- Verify MySQL is running
- Check username/password
- Ensure database exists

### Frontend not connecting

- Verify backend is running
- Check browser console for errors
- Confirm API URL in `app.js`

### No expenses calculated

- Ensure you have entries for consecutive days
- Expenses require previous day's balance to calculate

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 Your Name

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👨‍💻 Author

**Your Name**
- GitHub: [@Vishnu Sagar](https://github.com/Vishnu-Sagar-Pandruvada)
- LinkedIn: [Vishnu Sagar](https://www.linkedin.com/in/vishnu-sagar-pandruvada-3b7023250)
- Email: vsagar.available@gmail.com

---



<p align="center">
  Made with ❤️ for simpler expense tracking
</p>

<p align="center">
  ⭐ Star this repo if you find it helpful!
</p>
