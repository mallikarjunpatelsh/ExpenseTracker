package org.zobaze.assignment.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.zobaze.assignment.navigation.ExpenseTrackerNavigation
import org.zobaze.assignment.navigation.Screen

data class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = listOf(
        BottomNavItem(
            screen = Screen.ExpenseEntry,
            title = "Add",
            icon = Icons.Default.Add,
            selectedIcon = Icons.Filled.Add
        ),
        BottomNavItem(
            screen = Screen.ExpenseList,
            title = "Expenses",
            icon = Icons.Default.List,
            selectedIcon = Icons.Filled.List
        ),
        BottomNavItem(
            screen = Screen.ExpenseReport,
            title = "Reports",
            icon = Icons.Default.Assessment,
            selectedIcon = Icons.Filled.Assessment
        )
    )
    
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { 
                        it.route == item.screen.route 
                    } == true
                    
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        ExpenseTrackerNavigation(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
