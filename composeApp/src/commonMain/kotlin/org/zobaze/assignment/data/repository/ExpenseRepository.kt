package org.zobaze.assignment.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import org.zobaze.assignment.data.model.Expense
import org.zobaze.assignment.data.model.ExpenseCategory
import org.zobaze.assignment.data.model.ExpenseSummary
import org.zobaze.assignment.data.model.WeeklyReport

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesByDate(date: LocalDate): Flow<List<Expense>>
    fun getTodayExpenses(): Flow<List<Expense>>
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>
    fun getExpensesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>
    
    suspend fun getTodayTotalAmount(): Double
    suspend fun getTotalAmountByDate(date: LocalDate): Double
    suspend fun getTodayExpenseCount(): Int
    
    suspend fun insertExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun deleteExpenseById(id: Long)
    
    suspend fun checkForDuplicateExpense(title: String, amount: Double, category: ExpenseCategory): Boolean
    suspend fun getWeeklyReport(startDate: LocalDate, endDate: LocalDate): WeeklyReport
    suspend fun getExpenseSummaryByDate(date: LocalDate): ExpenseSummary
}
