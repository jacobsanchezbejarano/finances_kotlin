package com.devssoft.accounting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accounts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AccountsList(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AccountsList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val context = LocalContext.current
        val dbHelper = remember { DatabaseHelper(context) }

        // Retrieve all transactions and accounts
        val initialTransactions = remember { dbHelper.getAllTransactions() }
        val transactions = remember { mutableStateListOf<Transaction>().apply { addAll(initialTransactions) } }

        val accounts = remember { mutableStateListOf<Account>().apply { addAll(dbHelper.getAllAccounts()) } }


        // Group accounts by type
        val assetsList = remember { mutableStateListOf<Account>().apply { addAll(accounts.filter { it.code in 10001..19999 }) } }
        val liabilitiesList = remember { mutableStateListOf<Account>().apply { addAll(accounts.filter { it.code in 20001..29999 }) } }
        val equityList = remember { mutableStateListOf<Account>().apply { addAll(accounts.filter { it.code in 30001..39999 }) } }
        val incomeList = remember { mutableStateListOf<Account>().apply { addAll(accounts.filter { it.code in 40001..49999 }) } }
        val costsList = remember { mutableStateListOf<Account>().apply { addAll(accounts.filter { it.code in 50001..59999 }) } }
        val expensesList = remember { mutableStateListOf<Account>().apply { addAll(accounts.filter { it.code in 60001..69999 }) } }

        val newAssetCode = (assetsList.maxByOrNull { it.code }?.code ?: 10000).coerceAtMost(19999) + 1
        val newLiabilityCode = (liabilitiesList.maxByOrNull { it.code }?.code ?: 20000).coerceAtMost(29999) + 1
        val newEquityCode = (equityList.maxByOrNull { it.code }?.code ?: 30000).coerceAtMost(39999) + 1
        val newIncomeCode = (incomeList.maxByOrNull { it.code }?.code ?: 40000).coerceAtMost(49999) + 1
        val newCostCode = (costsList.maxByOrNull { it.code }?.code ?: 50000).coerceAtMost(59999) + 1
        val newExpenseCode = (expensesList.maxByOrNull { it.code }?.code ?: 60000).coerceAtMost(69999) + 1

        var newAccountName by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        var selectedType by remember { mutableStateOf<Int?>(null) }
        var selectedName by remember { mutableStateOf<String?>("") }



        fun addNewAccount(newAccount: Account) {
            // Add to the list in memory
            accounts.add(newAccount)
            // Insert into the database
            dbHelper.insertAccount(newAccount)
            newAccountName = ""
            selectedName = ""
        }

        // New account creation section
        OutlinedTextField(
            value = newAccountName,
            onValueChange = { newAccountName = it },
            label = { Text("New Account Name") },
            modifier = Modifier.fillMaxWidth()
        )

            Text("Type: $selectedName")
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select a type of account")
            }

            // List of account types with corresponding codes
            val accountTypes = listOf(
                Pair(1, "Assets"),
                Pair(2, "Liabilities"),
                Pair(3, "Equity"),
                Pair(4, "Income"),
                Pair(5, "Costs"),
                Pair(6, "Expenses")
            )


            // DropdownMenu for Debit
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                accountTypes.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.second) },
                        onClick = {
                            selectedType = option.first
                            selectedName = option.second
                            expanded = false
                        }
                    )
                }
            }

        Button(onClick = {
            // Create a new account object with appropriate code and name

            val newAccount = when (selectedType) {
                    1 -> Account(0, newAssetCode, newAccountName)
                    2 -> Account(0, newLiabilityCode, newAccountName)
                    3 -> Account(0, newEquityCode, newAccountName)
                    4 -> Account(0, newIncomeCode, newAccountName)
                    5 -> Account(0, newCostCode, newAccountName)
                    6 -> Account(0, newExpenseCode, newAccountName)
                    else -> throw IllegalArgumentException("Invalid account type")
            }

            // Add the new account to the appropriate list and the main accounts list
            when (selectedType) {
                1 -> assetsList.add(newAccount)
                2 -> liabilitiesList.add(newAccount)
                3 -> equityList.add(newAccount)
                4 -> incomeList.add(newAccount)
                5 -> costsList.add(newAccount)
                6 -> expensesList.add(newAccount)
            }

            addNewAccount(newAccount)


            // Update the UI to reflect the changes (e.g., refresh the list of accounts)
            // ...
        }) {
            Text("Create Account")
        }

        // Calculate balances for each account type
        val assetsBalances = calculateBalances(transactions, assetsList, 1)
        val liabilitiesBalances = calculateBalances(transactions, liabilitiesList, 2)
        val equityBalances = calculateBalances(transactions, equityList, 3)
        val incomeBalances = calculateBalances(transactions, incomeList, 4)
        val costsBalances = calculateBalances(transactions, costsList, 5)
        val expensesBalances = calculateBalances(transactions, expensesList, 6)

        // Display account balances
        Text("ASSETS")
        assetsBalances.forEach { (account, balance) ->
            Text("${account.name}: $balance")
        }
        Text("LIABILITIES")
        liabilitiesBalances.forEach { (account, balance) ->
            Text("${account.name}: $balance")
        }
        Text("EQUITY")
        equityBalances.forEach { (account, balance) ->
            Text("${account.name}: $balance")
        }
        Text("INCOME")
        incomeBalances.forEach { (account, balance) ->
            Text("${account.name}: $balance")
        }
        Text("COSTS")
        costsBalances.forEach { (account, balance) ->
            Text("${account.name}: $balance")
        }
        Text("EXPENSES")
        expensesBalances.forEach { (account, balance) ->
            Text("${account.name}: $balance")
        }
    }
}

// Function to calculate balances for a given list of accounts
fun calculateBalances(transactions: List<Transaction>, accounts: List<Account>, type: Int): Map<Account, Double> {
    val balances = mutableMapOf<Account, Double>()
    accounts.forEach { account -> balances[account] = 0.0 }

    transactions.forEach { transaction ->
        val account = accounts.find { it.code == transaction.code } ?: return@forEach
        val amount = if (type == 1 || type == 5 || type == 6) {
            transaction.amount * (if (transaction.type == TransactionType.DEBIT) 1 else -1)
        } else {
            transaction.amount * (if (transaction.type == TransactionType.CREDIT) 1 else -1)
        }
        balances[account] = balances[account]!! + amount
    }

    return balances
}

