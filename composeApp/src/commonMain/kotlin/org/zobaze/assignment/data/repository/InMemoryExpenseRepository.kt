package org.zobaze.assignment.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.zobaze.assignment.data.model.Expense
import org.zobaze.assignment.data.model.ExpenseCategory
import org.zobaze.assignment.data.model.ExpenseSummary
import org.zobaze.assignment.data.model.WeeklyReport
import org.zobaze.assignment.data.model.CategorySummary

class InMemoryExpenseRepository : ExpenseRepository {
    
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    private var nextId = 1L
    
    override fun getAllExpenses(): Flow<List<Expense>> = _expenses
    
    override fun getExpensesByDate(date: LocalDate): Flow<List<Expense>> =
        _expenses.map { expenses ->
            expenses.filter { expense ->
                expense.createdAt.date == date
            }
        }
    
    override fun getTodayExpenses(): Flow<List<Expense>> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return getExpensesByDate(today)
    }
    
    override fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>> =
        _expenses.map { expenses ->
            expenses.filter { it.category == category }
        }
    
    override fun getExpensesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> =
        _expenses.map { expenses ->
            expenses.filter { expense ->
                val expenseDate = expense.createdAt.date
                expenseDate >= startDate && expenseDate <= endDate
            }
        }
    
    override suspend fun getTodayTotalAmount(): Double {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return _expenses.value
            .filter { it.createdAt.date == today }
            .sumOf { it.amount }
    }
    
    override suspend fun getTotalAmountByDate(date: LocalDate): Double =
        _expenses.value
            .filter { it.createdAt.date == date }
            .sumOf { it.amount }
    
    override suspend fun getTodayExpenseCount(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return _expenses.value.count { it.createdAt.date == today }
    }
    
    override suspend fun insertExpense(expense: Expense): Long {
        val newExpense = expense.copy(id = nextId++)
        val currentExpenses = _expenses.value.toMutableList()
        currentExpenses.add(newExpense)
        _expenses.value = currentExpenses.sortedByDescending { it.createdAt }
        return newExpense.id
    }
    
    override suspend fun updateExpense(expense: Expense) {
        val currentExpenses = _expenses.value.toMutableList()
        val index = currentExpenses.indexOfFirst { it.id == expense.id }
        if (index != -1) {
            currentExpenses[index] = expense
            _expenses.value = currentExpenses.sortedByDescending { it.createdAt }
        }
    }
    
    override suspend fun deleteExpense(expense: Expense) {
        val currentExpenses = _expenses.value.toMutableList()
        currentExpenses.removeAll { it.id == expense.id }
        _expenses.value = currentExpenses
    }
    
    override suspend fun deleteExpenseById(id: Long) {
        val currentExpenses = _expenses.value.toMutableList()
        currentExpenses.removeAll { it.id == id }
        _expenses.value = currentExpenses
    }
    
    override suspend fun checkForDuplicateExpense(title: String, amount: Double, category: ExpenseCategory): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return _expenses.value.any { expense ->
            expense.title == title && 
            expense.amount == amount && 
            expense.category == category &&
            expense.createdAt.date == today
        }
    }
    
    override suspend fun getWeeklyReport(startDate: LocalDate, endDate: LocalDate): WeeklyReport {
        val expenses = _expenses.value.filter { expense ->
            val expenseDate = expense.createdAt.date
            expenseDate >= startDate && expenseDate <= endDate
        }
        
        // Group expenses by date
        val expensesByDate = expenses.groupBy { expense ->
            expense.createdAt.date
        }
        
        // Create daily summaries
        val dailySummaries = mutableListOf<ExpenseSummary>()
        var currentDate = startDate
        while (currentDate <= endDate) {
            val dayExpenses = expensesByDate[currentDate] ?: emptyList()
            val categoryBreakdown = dayExpenses.groupBy { it.category }
                .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
            
            dailySummaries.add(
                ExpenseSummary(
                    date = currentDate,
                    totalAmount = dayExpenses.sumOf { it.amount },
                    expenseCount = dayExpenses.size,
                    categoryBreakdown = categoryBreakdown
                )
            )
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }
        
        // Create category summaries
        val totalAmount = expenses.sumOf { it.amount }
        val categorySummaries = expenses.groupBy { it.category }
            .map { (category, categoryExpenses) ->
                val categoryTotal = categoryExpenses.sumOf { it.amount }
                CategorySummary(
                    category = category,
                    totalAmount = categoryTotal,
                    expenseCount = categoryExpenses.size,
                    percentage = if (totalAmount > 0) (categoryTotal / totalAmount) * 100 else 0.0
                )
            }
        
        return WeeklyReport(
            startDate = startDate,
            endDate = endDate,
            dailySummaries = dailySummaries,
            categorySummaries = categorySummaries,
            totalAmount = totalAmount,
            totalExpenses = expenses.size
        )
    }
    
    override suspend fun getExpenseSummaryByDate(date: LocalDate): ExpenseSummary {
        val expenses = _expenses.value.filter { it.createdAt.date == date }
        val categoryBreakdown = expenses.groupBy { it.category }
            .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
        
        return ExpenseSummary(
            date = date,
            totalAmount = expenses.sumOf { it.amount },
            expenseCount = expenses.size,
            categoryBreakdown = categoryBreakdown
        )
    }
    
    // Helper function to add some sample data for testing
    fun addSampleData() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timezone = TimeZone.currentSystemDefault()
        
        _expenses.value = _expenses.value + listOf(
            Expense(
                id = nextId++,
                title = "Team lunch",
                amount = 250.0,
                category = ExpenseCategory.FOOD,
                notes = "Team lunch at nearby restaurant",
                createdAt = now.toInstant(timezone).minus(1, DateTimeUnit.HOUR, timezone).toLocalDateTime(timezone)
            ),
            Expense(
                id = nextId++,
                title = "Client meeting transport",
                amount = 120.0,
                category = ExpenseCategory.TRAVEL,
                notes = "Client meeting transportation",
                createdAt = now.toInstant(timezone).minus(2, DateTimeUnit.HOUR, timezone).toLocalDateTime(timezone)
            ),
            Expense(
                id = nextId++,
                title = "Performance bonus",
                amount = 500.0,
                category = ExpenseCategory.STAFF,
                notes = "Performance bonus for team member",
                createdAt = now.toInstant(timezone).minus(1, DateTimeUnit.DAY, timezone).toLocalDateTime(timezone)
            ),
            Expense(
                id = nextId++,
                title = "Office supplies",
                amount = 85.0,
                category = ExpenseCategory.UTILITY,
                notes = "Printer paper and stationery",
                createdAt = now.toInstant(timezone).minus(3, DateTimeUnit.HOUR, timezone).toLocalDateTime(timezone)
            ),
            Expense(
                id = nextId++,
                title = "Monthly salary - John",
                amount = 3500.0,
                category = ExpenseCategory.STAFF,
                notes = "Software developer salary",
                createdAt = now.toInstant(timezone).minus(2, DateTimeUnit.DAY, timezone).toLocalDateTime(timezone)
            )
        ).sortedByDescending { it.createdAt }
    }
}
