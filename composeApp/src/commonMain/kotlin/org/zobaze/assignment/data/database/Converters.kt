package org.zobaze.assignment.data.database

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import org.zobaze.assignment.data.model.ExpenseCategory

class Converters {
    
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }
    
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString)
    }
    
    @TypeConverter
    fun fromExpenseCategory(category: ExpenseCategory): String {
        return category.name
    }
    
    @TypeConverter
    fun toExpenseCategory(categoryString: String): ExpenseCategory {
        return ExpenseCategory.valueOf(categoryString)
    }
}
