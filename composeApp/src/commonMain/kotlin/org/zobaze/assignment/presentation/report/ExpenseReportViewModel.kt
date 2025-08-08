package org.zobaze.assignment.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.zobaze.assignment.data.model.WeeklyReport
import org.zobaze.assignment.data.repository.ExpenseRepository
import org.zobaze.assignment.utils.formatAmount

data class ChartDataPoint(
    val label: String,
    val value: Double,
    val date: LocalDate? = null
)

data class ExpenseReportUiState(
    val weeklyReport: WeeklyReport? = null,
    val chartData: List<ChartDataPoint> = emptyList(),
    val categoryChartData: List<ChartDataPoint> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showExportOptions: Boolean = false,
    val exportInProgress: Boolean = false
)

class ExpenseReportViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExpenseReportUiState())
    val uiState: StateFlow<ExpenseReportUiState> = _uiState.asStateFlow()
    
    init {
        loadWeeklyReport()
    }
    
    private fun loadWeeklyReport() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val startDate = today.minus(6, DateTimeUnit.DAY) // Last 7 days including today
                val endDate = today
                
                val report = repository.getWeeklyReport(startDate, endDate)
                
                // Prepare chart data for daily totals
                val chartData = report.dailySummaries.map { summary ->
                    ChartDataPoint(
                        label = "${summary.date.month.name.take(3)} ${summary.date.dayOfMonth}",
                        value = summary.totalAmount,
                        date = summary.date
                    )
                }
                
                // Prepare chart data for category breakdown
                val categoryChartData = report.categorySummaries.map { summary ->
                    ChartDataPoint(
                        label = summary.category.displayName,
                        value = summary.totalAmount
                    )
                }
                
                _uiState.value = _uiState.value.copy(
                    weeklyReport = report,
                    chartData = chartData,
                    categoryChartData = categoryChartData,
                    isLoading = false
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load report: ${e.message}"
                )
            }
        }
    }
    
    fun refreshReport() {
        loadWeeklyReport()
    }
    
    fun showExportOptions() {
        _uiState.value = _uiState.value.copy(showExportOptions = true)
    }
    
    fun hideExportOptions() {
        _uiState.value = _uiState.value.copy(showExportOptions = false)
    }
    
    fun exportToPdf(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(exportInProgress = true)
                
                // Mock PDF export - in real implementation, you would generate actual PDF
                kotlinx.coroutines.delay(2000) // Simulate processing time
                
                val fileName = "expense_report_${Clock.System.todayIn(TimeZone.currentSystemDefault())}.pdf"
                
                _uiState.value = _uiState.value.copy(
                    exportInProgress = false,
                    showExportOptions = false
                )
                
                onSuccess("PDF exported successfully: $fileName")
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    exportInProgress = false,
                    showExportOptions = false
                )
                onError("Failed to export PDF: ${e.message}")
            }
        }
    }
    
    fun exportToCsv(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(exportInProgress = true)
                
                val report = _uiState.value.weeklyReport
                if (report == null) {
                    onError("No report data available")
                    return@launch
                }
                
                // Mock CSV generation - in real implementation, you would create actual CSV
                val csvContent = generateCsvContent(report)
                kotlinx.coroutines.delay(1000) // Simulate processing time
                
                val fileName = "expense_report_${Clock.System.todayIn(TimeZone.currentSystemDefault())}.csv"
                
                _uiState.value = _uiState.value.copy(
                    exportInProgress = false,
                    showExportOptions = false
                )
                
                onSuccess("CSV exported successfully: $fileName\n\nContent preview:\n${csvContent.take(200)}...")
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    exportInProgress = false,
                    showExportOptions = false
                )
                onError("Failed to export CSV: ${e.message}")
            }
        }
    }
    
    private fun generateCsvContent(report: WeeklyReport): String {
        val header = "Date,Total Amount,Expense Count,Staff,Travel,Food,Utility\n"
        val rows = report.dailySummaries.joinToString("\n") { summary ->
            val categoryAmounts = org.zobaze.assignment.data.model.ExpenseCategory.values().map { category ->
                summary.categoryBreakdown[category] ?: 0.0
            }
            "${summary.date},${formatAmount(summary.totalAmount)},${summary.expenseCount},${categoryAmounts.joinToString(",")}"
        }
        return header + rows
    }
    
    fun shareReport(onShare: (String) -> Unit) {
        val report = _uiState.value.weeklyReport ?: return
        
        val shareText = buildString {
            appendLine("📊 Weekly Expense Report")
            appendLine("Period: ${report.startDate} to ${report.endDate}")
            appendLine()
            appendLine("💰 Total Amount: ₹${formatAmount(report.totalAmount)}")
            appendLine("📝 Total Expenses: ${report.totalExpenses}")
            appendLine()
            appendLine("📈 Category Breakdown:")
            report.categorySummaries.forEach { category ->
                appendLine("${category.category.emoji} ${category.category.displayName}: ₹${formatAmount(category.totalAmount)} (${formatAmount(category.percentage)}%)")
            }
        }
        
        onShare(shareText)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
