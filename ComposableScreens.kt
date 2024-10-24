// ComposableScreens.kt
package com.devssoft.accounting

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.devssoft.accounting.ui.theme.AccountingTheme
import androidx.navigation.compose.*


@Composable
fun AccountingApp(userName: String) {

    AccountingTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) {
                HomeScreen(
                    modifier = Modifier.padding(),
                    onNavigateToTransactions = {
                        navController.navigate(Screen.Transactions.route)
                    },
                    onNavigateToAnalysis = {
                        navController.navigate(Screen.Analysis.route)
                    },
                    onNavigateToAccounts = {
                        navController.navigate(Screen.Accounts.route)
                    },
                    userName
                )
            }
            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Analysis.route) {
                AnalysisScreen( onBack = { navController.popBackStack() } )
            }

            composable(Screen.Accounts.route) {
                AccountsScreen( onBack = { navController.popBackStack() } )
            }
        }
    }
}



