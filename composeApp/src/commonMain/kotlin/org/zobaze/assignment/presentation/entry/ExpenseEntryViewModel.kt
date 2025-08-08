package org.zobaze.assignment.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.zobaze.assignment.data.model.Expense
import org.zobaze.assignment.data.model.ExpenseCategory
import org.zobaze.assignment.data.repository.ExpenseRepository

data class ExpenseEntryUiState(
    val title: String = "",
    val amount: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.FOOD,
    val notes: String = "",
    val receiptImagePath: String? = null,
    val todayTotalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false,
    val showDuplicateWarning: Boolean = false
)

class ExpenseEntryViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExpenseEntryUiState())
    val uiState: StateFlow<ExpenseEntryUiState> = _uiState.asStateFlow()
    
    init {
        loadTodayTotal()
        observeFormValidation()
    }
    
    private fun observeFormValidation() {
        viewModelScope.launch {
            combine(
                _uiState,
                _uiState
            ) { state, _ ->
                validateForm(state)
            }.collect { isValid ->
                _uiState.value = _uiState.value.copy(isFormValid = isValid)
            }
        }
    }
    
    private fun validateForm(state: ExpenseEntryUiState): Boolean {
        val titleValid = state.title.trim().isNotEmpty()
        val amountValid = state.amount.toDoubleOrNull()?.let { it > 0 } ?: false
        return titleValid && amountValid
    }
    
    private fun loadTodayTotal() {
        viewModelScope.launch {
            try {
                val total = repository.getTodayTotalAmount()
                _uiState.value = _uiState.value.copy(todayTotalAmount = total)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load today's total: ${e.message}"
                )
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            errorMessage = null
        )
    }
    
    fun updateAmount(amount: String) {
        // Allow only valid decimal numbers
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.value = _uiState.value.copy(
                amount = amount,
                errorMessage = null
            )
        }
    }
    
    fun updateCategory(category: ExpenseCategory) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            showDuplicateWarning = false
        )
    }
    
    fun updateNotes(notes: String) {
        if (notes.length <= 100) {
            _uiState.value = _uiState.value.copy(
                notes = notes,
                errorMessage = null
            )
        }
    }
    
    fun updateReceiptImage(imagePath: String?) {
        _uiState.value = _uiState.value.copy(receiptImagePath = imagePath)
    }
    
    fun submitExpense(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        
        if (!state.isFormValid) {
            onError("Please fill in all required fields correctly")
            return
        }
        
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            onError("Please enter a valid amount")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, errorMessage = null)
                
                // Check for duplicates
                val isDuplicate = repository.checkForDuplicateExpense(
                    title = state.title.trim(),
                    amount = amount,
                    category = state.selectedCategory
                )
                
                if (isDuplicate) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        showDuplicateWarning = true
                    )
                    onError("Similar expense already exists today. Continue anyway?")
                    return@launch
                }
                
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val expense = Expense(
                    title = state.title.trim(),
                    amount = amount,
                    category = state.selectedCategory,
                    notes = state.notes.trim(),
                    receiptImagePath = state.receiptImagePath,
                    createdAt = now
                )
                
                repository.insertExpense(expense)
                
                // Reset form
                _uiState.value = ExpenseEntryUiState()
                loadTodayTotal()
                
                onSuccess()
                
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                onError("Failed to save expense: ${e.message}")
            }
        }
    }
    
    fun submitExpenseIgnoringDuplicate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, showDuplicateWarning = false)
                
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val expense = Expense(
                    title = state.title.trim(),
                    amount = amount,
                    category = state.selectedCategory,
                    notes = state.notes.trim(),
                    receiptImagePath = state.receiptImagePath,
                    createdAt = now
                )
                
                repository.insertExpense(expense)
                
                // Reset form
                _uiState.value = ExpenseEntryUiState()
                loadTodayTotal()
                
                onSuccess()
                
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                onError("Failed to save expense: ${e.message}")
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun dismissDuplicateWarning() {
        _uiState.value = _uiState.value.copy(showDuplicateWarning = false)
    }
}
