package org.zobaze.assignment.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.zobaze.assignment.data.model.Expense

@Database(
    entities = [Expense::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    
    companion object {
        const val DATABASE_NAME = "expense_database.db"
    }
}

// Platform-specific database builder will be implemented in platform-specific modules
expect fun getDatabaseBuilder(): RoomDatabase.Builder<ExpenseDatabase>
