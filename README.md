# 💰 Smart Daily Expense Tracker

## App Overview
A modern Kotlin Multiplatform expense tracking application built with Compose Multiplatform, targeting both Android and iOS platforms. The app enables small business owners to efficiently track daily expenses with categories like Staff, Travel, Food, and Utility, featuring real-time analytics, export capabilities, and a clean Material Design 3 interface.

## AI Usage Summary
This project was developed with extensive AI assistance using Cascade (Windsurf AI assistant). AI was utilized for:
- Complete project architecture design and Kotlin Multiplatform setup with Room KMP database integration
- Full UI implementation using Compose Multiplatform with MVVM architecture and Koin dependency injection
- Debugging and resolving complex multiplatform compatibility issues, including platform-specific implementations for iOS/Android
- Creating comprehensive business logic for expense management, filtering, reporting, and data export functionality
- Troubleshooting build configuration issues, dependency conflicts, and iOS framework compilation problems

## Key Development Prompts
```
1. "Create a Smart Daily Expense Tracker using Kotlin Multiplatform targeting Android and iOS. 
   Include expense entry, list management, and reporting features with Room database integration."

2. "Implement MVVM architecture with Compose Multiplatform UI, Koin dependency injection, 
   and proper navigation between expense entry, list, and reports screens."

3. "Set up Room KMP database with proper entity relationships, DAO operations, and 
   multiplatform compatibility for both Android and iOS targets."

4. "Create dedicated Android application module structure since composeApp should be 
   a shared library, not a runnable application."

5. "Fix iOS build compilation errors by implementing platform-specific solutions for 
   String.format(), System.currentTimeMillis(), and Koin ViewModel compatibility."

6. "Resolve navigation library dependency conflicts and cache build failures for 
   iOS framework generation in Xcode."
```

## ✅ Features Implemented

### Core Functionality
- [x] **Expense Entry Screen** - Add expenses with amount, title, category selection, and notes
- [x] **Expense List Screen** - View all expenses with filtering by date and category
- [x] **Expense Reports Screen** - 7-day analytics with category breakdowns and charts
- [x] **Data Persistence** - Room KMP database for Android, in-memory storage for iOS
- [x] **Export Functionality** - CSV export and formatted report sharing
- [x] **Real-time Totals** - Live calculation of daily and weekly expense totals

### Technical Features
- [x] **Kotlin Multiplatform** - Shared codebase for Android and iOS
- [x] **Compose Multiplatform UI** - Modern Material Design 3 interface
- [x] **MVVM Architecture** - Clean separation with ViewModels and StateFlow
- [x] **Koin Dependency Injection** - Modular dependency management
- [x] **Navigation Component** - Multi-screen navigation with bottom tabs
- [x] **Form Validation** - Input validation with user-friendly error messages
- [x] **Duplicate Detection** - Smart duplicate expense prevention
- [x] **Platform-specific Implementations** - expect/actual pattern for iOS/Android compatibility

### Categories & Business Logic
- [x] **Staff Expenses** - Employee-related expense tracking
- [x] **Travel Expenses** - Transportation and travel cost management
- [x] **Food Expenses** - Meal and dining expense tracking
- [x] **Utility Expenses** - Bills and utility payment tracking
- [x] **Category-wise Analytics** - Breakdown by expense categories
- [x] **Date-based Filtering** - Filter expenses by date ranges

## 📱 APK Download
[Download APK](https://github.com/mallikarjunpatelsh/ExpenseTracker/blob/master/androidApp-debug.apk) - Android Debug APK for testing

## 📸 Screenshots
*Screenshots will be added here once the app is fully deployed*

### Screenshots
- Expense Entry Screen
- Expense List with Filtering
- Weekly Reports Dashboard
- Category Analytics

## 🏗️ Technical Architecture

### Tech Stack
- **Language**: Kotlin 2.2.0
- **UI Framework**: Compose Multiplatform 1.8.2
- **Database**: Room KMP 2.7.2
- **Dependency Injection**: Koin 4.0.0
- **Navigation**: Navigation Compose (Multiplatform)
- **Date/Time**: kotlinx-datetime
- **Serialization**: kotlinx-serialization

### Project Structure
```
ExpenseTracker/
├── composeApp/          # Shared Kotlin Multiplatform library
│   ├── commonMain/      # Shared business logic and UI
│   ├── androidMain/     # Android-specific implementations
│   └── iosMain/         # iOS-specific implementations
├── androidApp/          # Android application wrapper
└── iosApp/             # iOS application wrapper
```

## 🛠️ Setup & Installation

### Prerequisites
- **Android Studio**: Arctic Fox or later
- **Xcode**: 14.0 or later (for iOS development)
- **JDK**: 17 or later
- **Kotlin**: 2.2.0
- **Gradle**: 8.9

## 📱 Usage Guide

### Adding Expenses
1. Open the app and navigate to the "Add Expense" tab
2. Enter the expense amount, title, and select a category
3. Optionally add notes and receipt information
4. Tap "Add Expense" to save

### Managing Expenses
1. Navigate to the "Expenses" tab to view all expenses
2. Use filters to find specific expenses by date or category
3. Tap on any expense to view details
4. Use the delete button to remove expenses

### Viewing Reports
1. Go to the "Reports" tab for expense analytics
2. View 7-day summaries with category breakdowns
3. Export data as CSV or share formatted reports
4. Analyze spending patterns with visual charts

### Key Components
- **ExpenseEntryViewModel**: Handles expense creation and validation
- **ExpenseListViewModel**: Manages expense list and filtering
- **ExpenseReportViewModel**: Generates reports and analytics
- **ExpenseRepository**: Data access abstraction layer
- **ExpenseDatabase**: Room database configuration

## 🙏 Acknowledgments

- Built with [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- UI powered by [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Database by [Room KMP](https://developer.android.com/kotlin/multiplatform/room)
- Dependency Injection by [Koin](https://insert-koin.io/)
- AI Development Assistant: [Cascade by Windsurf](https://codeium.com/windsurf)

---

**Made with ❤️ using Kotlin Multiplatform and AI assistance**
