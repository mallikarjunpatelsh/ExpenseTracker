package org.zobaze.assignment.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Entity(tableName = "expenses")
@Serializable
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val notes: String = "",
    val receiptImagePath: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime = createdAt
)
