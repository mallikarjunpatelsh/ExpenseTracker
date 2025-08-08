package org.zobaze.assignment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.zobaze.assignment.presentation.entry.ExpenseEntryScreen
import org.zobaze.assignment.presentation.list.ExpenseListScreen
import org.zobaze.assignment.presentation.report.ExpenseReportScreen

sealed class Screen(val route: String) {
    object ExpenseEntry : Screen("expense_entry")
    object ExpenseList : Screen("expense_list")
    object ExpenseReport : Screen("expense_report")
}

@Composable
fun ExpenseTrackerNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.ExpenseEntry.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.ExpenseEntry.route) {
            ExpenseEntryScreen()
        }
        
        composable(Screen.ExpenseList.route) {
            ExpenseListScreen()
        }
        
        composable(Screen.ExpenseReport.route) {
            ExpenseReportScreen()
        }
    }
}
