package org.zobaze.assignment.presentation.entry

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.zobaze.assignment.presentation.components.AmountInput
import org.zobaze.assignment.presentation.components.CategorySelector
import org.zobaze.assignment.utils.formatAmount
import org.zobaze.assignment.utils.getCurrentTimeMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpenseEntryViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Animation for the total amount card
    val totalAmountScale by animateFloatAsState(
        targetValue = if (uiState.todayTotalAmount > 0) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            kotlinx.coroutines.delay(2000)
            showSuccessMessage = false
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Add New Expense",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Today's Total Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = totalAmountScale
                    scaleY = totalAmountScale
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Spent Today",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹${formatAmount(uiState.todayTotalAmount)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title Input
        OutlinedTextField(
            value = uiState.title,
            onValueChange = viewModel::updateTitle,
            label = { Text("Title *") },
            placeholder = { Text("e.g., Office lunch, Taxi fare") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            isError = uiState.title.isEmpty() && uiState.errorMessage != null
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Amount Input
        AmountInput(
            amount = uiState.amount,
            onAmountChange = viewModel::updateAmount,
            isError = uiState.amount.toDoubleOrNull()?.let { it <= 0 } ?: (uiState.amount.isNotEmpty())
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category Selector
        CategorySelector(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = viewModel::updateCategory
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Notes Input
        OutlinedTextField(
            value = uiState.notes,
            onValueChange = viewModel::updateNotes,
            label = { Text("Notes (Optional)") },
            placeholder = { Text("Additional details...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            supportingText = {
                Text("${uiState.notes.length}/100")
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Receipt Upload (Mock)
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Mock receipt upload
                viewModel.updateReceiptImage("mock_receipt_${getCurrentTimeMillis()}.jpg")
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Add receipt",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (uiState.receiptImagePath != null) "Receipt Added" else "Add Receipt (Optional)",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (uiState.receiptImagePath != null) FontWeight.Medium else FontWeight.Normal
                    )
                    if (uiState.receiptImagePath != null) {
                        Text(
                            text = uiState.receiptImagePath!!.substringAfterLast("/"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Submit Button
        AnimatedVisibility(
            visible = !uiState.isLoading,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Button(
                onClick = {
                    viewModel.submitExpense(
                        onSuccess = {
                            showSuccessMessage = true
                        },
                        onError = { error ->
                            errorMessage = error
                            showErrorSnackbar = true
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState.isFormValid && !uiState.isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Expense",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        
        // Loading indicator
        AnimatedVisibility(
            visible = uiState.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
    
    // Success Message
    AnimatedVisibility(
        visible = showSuccessMessage,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✅",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Expense added successfully!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
    
    // Duplicate Warning Dialog
    if (uiState.showDuplicateWarning) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDuplicateWarning,
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Duplicate Expense Detected") },
            text = { 
                Text("A similar expense already exists today. Do you want to add it anyway?") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.submitExpenseIgnoringDuplicate(
                            onSuccess = {
                                showSuccessMessage = true
                            },
                            onError = { error ->
                                errorMessage = error
                                showErrorSnackbar = true
                            }
                        )
                    }
                ) {
                    Text("Add Anyway")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDuplicateWarning) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Error Snackbar
    if (showErrorSnackbar) {
        LaunchedEffect(showErrorSnackbar) {
            kotlinx.coroutines.delay(3000)
            showErrorSnackbar = false
        }
    }
}
