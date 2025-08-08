package org.zobaze.assignment.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.zobaze.assignment.data.model.Expense
import org.zobaze.assignment.data.model.ExpenseCategory

@Dao
interface ExpenseDao {
    
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE DATE(createdAt) = :date ORDER BY createdAt DESC")
    fun getExpensesByDate(date: String): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE DATE(createdAt) = DATE('now', 'localtime') ORDER BY createdAt DESC")
    fun getTodayExpenses(): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY createdAt DESC")
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE DATE(createdAt) BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<Expense>>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE DATE(createdAt) = DATE('now', 'localtime')")
    suspend fun getTodayTotalAmount(): Double?
    
    @Query("SELECT SUM(amount) FROM expenses WHERE DATE(createdAt) = :date")
    suspend fun getTotalAmountByDate(date: String): Double?
    
    @Query("SELECT COUNT(*) FROM expenses WHERE DATE(createdAt) = DATE('now', 'localtime')")
    suspend fun getTodayExpenseCount(): Int
    
    @Query("SELECT * FROM expenses WHERE title = :title AND amount = :amount AND category = :category AND DATE(createdAt) = DATE('now', 'localtime')")
    suspend fun findDuplicateExpense(title: String, amount: Double, category: ExpenseCategory): List<Expense>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long
    
    @Update
    suspend fun updateExpense(expense: Expense)
    
    @Delete
    suspend fun deleteExpense(expense: Expense)
    
    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)
    
    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}
