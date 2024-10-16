package com.devssoft.accounting

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object Analysis : Screen("analysis")
    object Accounts : Screen("accounts")
}
