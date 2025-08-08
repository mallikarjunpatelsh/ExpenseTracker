package org.zobaze.assignment.presentation.report

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.zobaze.assignment.utils.formatAmount
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpenseReportViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSuccessMessage by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var showErrorMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            kotlinx.coroutines.delay(3000)
            showSuccessMessage = false
        }
    }
    
    LaunchedEffect(showErrorMessage) {
        if (showErrorMessage) {
            kotlinx.coroutines.delay(3000)
            showErrorMessage = false
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Report",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    IconButton(
                        onClick = { viewModel.refreshReport() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    
                    IconButton(
                        onClick = { viewModel.showExportOptions() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export"
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            viewModel.shareReport { shareText ->
                                // Mock share functionality
                                successMessage = "Report shared successfully!"
                                showSuccessMessage = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            }
        }
        
        // Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.weeklyReport == null -> {
                EmptyReportState(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Summary Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SummaryCard(
                                title = "Total Amount",
                                value = "₹${formatAmount(uiState.weeklyReport!!.totalAmount)}",
                                icon = Icons.Default.MonetizationOn,
                                modifier = Modifier.weight(1f)
                            )
                            
                            SummaryCard(
                                title = "Total Expenses",
                                value = "${uiState.weeklyReport!!.totalExpenses}",
                                icon = Icons.Default.Article,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    item {
                        // Daily Expenses Chart
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Daily Expenses (Last 7 Days)",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                BarChart(
                                    data = uiState.chartData,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }
                        }
                    }
                    
                    item {
                        // Category Breakdown
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Category Breakdown",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                if (uiState.categoryChartData.isNotEmpty()) {
                                    PieChart(
                                        data = uiState.categoryChartData,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )
                                } else {
                                    Text(
                                        text = "No category data available",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        // Category Details
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Category Details",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    
                    items(uiState.weeklyReport!!.categorySummaries) { categorySummary ->
                        CategoryDetailCard(categorySummary = categorySummary)
                    }
                }
            }
        }
    }
    
    // Export Options Bottom Sheet
    if (uiState.showExportOptions) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.hideExportOptions() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Export Options",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                ListItem(
                    headlineContent = { Text("Export as PDF") },
                    supportingContent = { Text("Generate a detailed PDF report") },
                    leadingContent = {
                        Icon(Icons.Default.PictureInPicture, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        viewModel.exportToPdf(
                            onSuccess = { message ->
                                successMessage = message
                                showSuccessMessage = true
                            },
                            onError = { error ->
                                errorMessage = error
                                showErrorMessage = true
                            }
                        )
                    }
                )
                
                ListItem(
                    headlineContent = { Text("Export as CSV") },
                    supportingContent = { Text("Export data in spreadsheet format") },
                    leadingContent = {
                        Icon(Icons.Default.GridOn, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        viewModel.exportToCsv(
                            onSuccess = { message ->
                                successMessage = message
                                showSuccessMessage = true
                            },
                            onError = { error ->
                                errorMessage = error
                                showErrorMessage = true
                            }
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Loading overlay for export
    if (uiState.exportInProgress) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Exporting report...")
                }
            }
        }
    }
    
    // Success/Error Messages
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
            Text(
                text = successMessage,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
    
    AnimatedVisibility(
        visible = showErrorMessage,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Text(
                text = errorMessage,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CategoryDetailCard(
    categorySummary: org.zobaze.assignment.data.model.CategorySummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categorySummary.category.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categorySummary.category.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${categorySummary.expenseCount} expenses",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${formatAmount(categorySummary.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${formatAmount(categorySummary.percentage)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyReportState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Analytics,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No data available",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "Add some expenses to see your report",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BarChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        
        val maxValue = data.maxOfOrNull { it.value } ?: 0.0
        if (maxValue <= 0) return@Canvas
        
        val barWidth = size.width / data.size * 0.7f
        val spacing = size.width / data.size * 0.3f
        
        data.forEachIndexed { index, dataPoint ->
            val barHeight = (dataPoint.value / maxValue * size.height * 0.8).toFloat()
            val x = index * (barWidth + spacing) + spacing / 2
            val y = size.height - barHeight
            
            // Draw bar
            drawRect(
                color = primaryColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
            
            // Draw label (simplified)
            drawRect(
                color = surfaceVariant,
                topLeft = Offset(x, size.height - 20f),
                size = Size(barWidth, 20f)
            )
        }
    }
}

@Composable
private fun PieChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error
    )
    
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        
        val total = data.sumOf { it.value }
        if (total <= 0) return@Canvas
        
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) / 2 * 0.8f
        
        var startAngle = 0f
        
        data.forEachIndexed { index, dataPoint ->
            val sweepAngle = (dataPoint.value / total * 360).toFloat()
            
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            
            startAngle += sweepAngle
        }
    }
}
