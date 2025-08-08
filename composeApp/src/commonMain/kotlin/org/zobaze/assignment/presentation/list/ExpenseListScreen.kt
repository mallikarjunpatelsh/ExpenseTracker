package org.zobaze.assignment.presentation.list

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import org.koin.compose.koinInject
import org.zobaze.assignment.presentation.components.ExpenseCard
import org.zobaze.assignment.utils.formatAmount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpenseListViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header with filters
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title and refresh button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Expenses",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = { viewModel.refreshExpenses() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.filterType == FilterType.TODAY,
                        onClick = { viewModel.updateFilterType(FilterType.TODAY) },
                        label = { Text("Today") },
                        leadingIcon = if (uiState.filterType == FilterType.TODAY) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                    
                    FilterChip(
                        selected = uiState.filterType == FilterType.CUSTOM_DATE,
                        onClick = { viewModel.showDatePicker() },
                        label = { 
                            Text(
                                if (uiState.filterType == FilterType.CUSTOM_DATE) {
                                    "${uiState.selectedDate.dayOfMonth}/${uiState.selectedDate.monthNumber}"
                                } else {
                                    "Date"
                                }
                            ) 
                        },
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    
                    FilterChip(
                        selected = uiState.filterType == FilterType.ALL,
                        onClick = { viewModel.updateFilterType(FilterType.ALL) },
                        label = { Text("All") }
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Grouping options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Group by:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    
                    FilterChip(
                        selected = uiState.groupingType == GroupingType.NONE,
                        onClick = { viewModel.updateGroupingType(GroupingType.NONE) },
                        label = { Text("None") }
                    )
                    
                    FilterChip(
                        selected = uiState.groupingType == GroupingType.CATEGORY,
                        onClick = { viewModel.updateGroupingType(GroupingType.CATEGORY) },
                        label = { Text("Category") }
                    )
                    
                    FilterChip(
                        selected = uiState.groupingType == GroupingType.TIME,
                        onClick = { viewModel.updateGroupingType(GroupingType.TIME) },
                        label = { Text("Time") }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Summary card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "₹${formatAmount(uiState.totalAmount)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Total Expenses",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "${uiState.totalCount}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        
        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.expenses.isEmpty() -> {
                    EmptyState(
                        filterType = uiState.filterType,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.groupingType == GroupingType.NONE -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.expenses,
                            key = { it.id }
                        ) { expense ->
                            ExpenseCard(
                                expense = expense,
                                onDelete = { viewModel.deleteExpense(expense) }
                            )
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.groupedExpenses.forEach { (groupTitle, expenses) ->
                            item(key = "header_$groupTitle") {
                                Text(
                                    text = groupTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            
                            items(
                                items = expenses,
                                key = { "${groupTitle}_${it.id}" }
                            ) { expense ->
                                ExpenseCard(
                                    expense = expense,
                                    onDelete = { viewModel.deleteExpense(expense) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }
    
    // Date picker (mock implementation)
    if (uiState.showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                viewModel.updateSelectedDate(date)
            },
            onDismiss = {
                viewModel.hideDatePicker()
            }
        )
    }
}

@Composable
private fun EmptyState(
    filterType: FilterType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Receipt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = when (filterType) {
                FilterType.TODAY -> "No expenses today"
                FilterType.CUSTOM_DATE -> "No expenses for selected date"
                FilterType.ALL -> "No expenses yet"
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Start by adding your first expense!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DatePickerDialog(
    onDateSelected: (kotlinx.datetime.LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    // Mock date picker - in real implementation, use actual date picker
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = { 
            Text("Mock date picker - selecting yesterday's date")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val yesterday = Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(1, DateTimeUnit.DAY)
                    onDateSelected(yesterday)
                }
            ) {
                Text("Select Yesterday")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
