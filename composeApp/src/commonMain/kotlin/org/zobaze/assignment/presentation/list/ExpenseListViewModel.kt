package org.zobaze.assignment.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.zobaze.assignment.data.model.Expense
import org.zobaze.assignment.data.model.ExpenseCategory
import org.zobaze.assignment.data.repository.ExpenseRepository

enum class GroupingType {
    NONE, CATEGORY, TIME
}

enum class FilterType {
    TODAY, CUSTOM_DATE, ALL
}

data class ExpenseListUiState(
    val expenses: List<Expense> = emptyList(),
    val groupedExpenses: Map<String, List<Expense>> = emptyMap(),
    val selectedDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val filterType: FilterType = FilterType.TODAY,
    val groupingType: GroupingType = GroupingType.NONE,
    val totalAmount: Double = 0.0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDatePicker: Boolean = false
)

class ExpenseListViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExpenseListUiState())
    val uiState: StateFlow<ExpenseListUiState> = _uiState.asStateFlow()
    
    init {
        loadExpenses()
    }
    
    private fun loadExpenses() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                val expensesFlow = when (_uiState.value.filterType) {
                    FilterType.TODAY -> repository.getTodayExpenses()
                    FilterType.CUSTOM_DATE -> repository.getExpensesByDate(_uiState.value.selectedDate)
                    FilterType.ALL -> repository.getAllExpenses()
                }
                
                expensesFlow.collect { expenses ->
                    val totalAmount = expenses.sumOf { it.amount }
                    val totalCount = expenses.size
                    val groupedExpenses = groupExpenses(expenses, _uiState.value.groupingType)
                    
                    _uiState.value = _uiState.value.copy(
                        expenses = expenses,
                        groupedExpenses = groupedExpenses,
                        totalAmount = totalAmount,
                        totalCount = totalCount,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load expenses: ${e.message}"
                )
            }
        }
    }
    
    private fun groupExpenses(expenses: List<Expense>, groupingType: GroupingType): Map<String, List<Expense>> {
        return when (groupingType) {
            GroupingType.NONE -> emptyMap()
            GroupingType.CATEGORY -> expenses.groupBy { "${it.category.emoji} ${it.category.displayName}" }
            GroupingType.TIME -> expenses.groupBy { 
                val date = it.createdAt.date
                "${date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, ${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}"
            }
        }
    }
    
    fun updateFilterType(filterType: FilterType) {
        _uiState.value = _uiState.value.copy(filterType = filterType)
        loadExpenses()
    }
    
    fun updateSelectedDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            filterType = FilterType.CUSTOM_DATE,
            showDatePicker = false
        )
        loadExpenses()
    }
    
    fun updateGroupingType(groupingType: GroupingType) {
        val currentExpenses = _uiState.value.expenses
        val groupedExpenses = groupExpenses(currentExpenses, groupingType)
        
        _uiState.value = _uiState.value.copy(
            groupingType = groupingType,
            groupedExpenses = groupedExpenses
        )
    }
    
    fun showDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = true)
    }
    
    fun hideDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = false)
    }
    
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expense)
                // Expenses will be automatically updated through the flow
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete expense: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun refreshExpenses() {
        loadExpenses()
    }
}
