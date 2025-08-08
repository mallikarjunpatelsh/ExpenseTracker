package org.zobaze.assignment.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.*
import org.zobaze.assignment.data.database.ExpenseDao
import org.zobaze.assignment.data.model.Expense
import org.zobaze.assignment.data.model.ExpenseCategory
import org.zobaze.assignment.data.model.ExpenseSummary
import org.zobaze.assignment.data.model.WeeklyReport
import org.zobaze.assignment.data.model.CategorySummary

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    
    override fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()
    
    override fun getExpensesByDate(date: LocalDate): Flow<List<Expense>> =
        expenseDao.getExpensesByDate(date.toString())
    
    override fun getTodayExpenses(): Flow<List<Expense>> = expenseDao.getTodayExpenses()
    
    override fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>> =
        expenseDao.getExpensesByCategory(category)
    
    override fun getExpensesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> =
        expenseDao.getExpensesBetweenDates(startDate.toString(), endDate.toString())
    
    override suspend fun getTodayTotalAmount(): Double =
        expenseDao.getTodayTotalAmount() ?: 0.0
    
    override suspend fun getTotalAmountByDate(date: LocalDate): Double =
        expenseDao.getTotalAmountByDate(date.toString()) ?: 0.0
    
    override suspend fun getTodayExpenseCount(): Int = expenseDao.getTodayExpenseCount()
    
    override suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    
    override suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    
    override suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
    
    override suspend fun deleteExpenseById(id: Long) = expenseDao.deleteExpenseById(id)
    
    override suspend fun checkForDuplicateExpense(title: String, amount: Double, category: ExpenseCategory): Boolean {
        val duplicates = expenseDao.findDuplicateExpense(title, amount, category)
        return duplicates.isNotEmpty()
    }
    
    override suspend fun getWeeklyReport(startDate: LocalDate, endDate: LocalDate): WeeklyReport {
        val expenses = getExpensesBetweenDates(startDate, endDate).first()
        
        // Group expenses by date
        val expensesByDate = expenses.groupBy { expense ->
            LocalDate.parse(expense.createdAt.date.toString())
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
        val expenses = getExpensesByDate(date).first()
        val categoryBreakdown = expenses.groupBy { it.category }
            .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
        
        return ExpenseSummary(
            date = date,
            totalAmount = expenses.sumOf { it.amount },
            expenseCount = expenses.size,
            categoryBreakdown = categoryBreakdown
        )
    }
}
