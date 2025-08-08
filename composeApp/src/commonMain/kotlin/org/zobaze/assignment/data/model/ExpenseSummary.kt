package org.zobaze.assignment.data.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ExpenseSummary(
    val date: LocalDate,
    val totalAmount: Double,
    val expenseCount: Int,
    val categoryBreakdown: Map<ExpenseCategory, Double>
)

@Serializable
data class CategorySummary(
    val category: ExpenseCategory,
    val totalAmount: Double,
    val expenseCount: Int,
    val percentage: Double
)

@Serializable
data class WeeklyReport(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val dailySummaries: List<ExpenseSummary>,
    val categorySummaries: List<CategorySummary>,
    val totalAmount: Double,
    val totalExpenses: Int
)
