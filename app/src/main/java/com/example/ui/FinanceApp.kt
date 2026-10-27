package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle

object Destinations {
    const val DASHBOARD = "dashboard"
    const val TRANSACTIONS = "transactions"
    const val ADD_TRANSACTION = "add_transaction"
}

@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Destinations.DASHBOARD

    Scaffold(
        bottomBar = {
            if (currentRoute != Destinations.ADD_TRANSACTION) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        selected = currentRoute == Destinations.DASHBOARD,
                        onClick = {
                            navController.navigate(Destinations.DASHBOARD) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.List, contentDescription = "Transaksi") },
                        label = { Text("Transaksi") },
                        selected = currentRoute == Destinations.TRANSACTIONS,
                        onClick = {
                            navController.navigate(Destinations.TRANSACTIONS) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == Destinations.DASHBOARD || currentRoute == Destinations.TRANSACTIONS) {
                FloatingActionButton(
                    onClick = { navController.navigate(Destinations.ADD_TRANSACTION) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Filled.Add, "Tambah Transaksi")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.DASHBOARD) {
                DashboardScreen(viewModel = viewModel)
            }
            composable(Destinations.TRANSACTIONS) {
                TransactionsScreen(
                    viewModel = viewModel,
                    onNavigateToEdit = {
                        navController.navigate(Destinations.ADD_TRANSACTION)
                    }
                )
            }
            composable(Destinations.ADD_TRANSACTION) {
                AddTransactionScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
