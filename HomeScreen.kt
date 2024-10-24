package com.devssoft.accounting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToAnalysis: () -> Unit = {},
    onNavigateToAccounts: () -> Unit = {},
    userName: String,
    onLogout: () -> Unit
) {

    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    val initialTransactions = remember { dbHelper.getAllTransactions() }

    // Use mutableStateListOf to manage the transactions list reactively
    val transactions = remember { mutableStateListOf<Transaction>().apply { addAll(initialTransactions) } }

    // Define available accounts
    val accounts = remember { mutableStateListOf<Account>().apply { addAll(dbHelper.getAllAccounts()) } }

    // States for input fields
    var selectedAccountDebit by remember { mutableStateOf<Account?>(null) }
    var selectedAccountCredit by remember { mutableStateOf<Account?>(null) }
    var amountInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("") }

    // State for error messages (optional)
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Function to add a transaction
    fun addTransaction(transactionType: TransactionType, entry: Int) {
        val newId = (transactions.maxOfOrNull { it.id } ?: 0) + 1

        val account = if (transactionType == TransactionType.DEBIT) selectedAccountDebit else selectedAccountCredit
        val amount = amountInput.toDoubleOrNull()
        val date = dateInput.ifEmpty { "2024-10-14 00:00:00" } // Default date

        when {
            account == null -> errorMessage = "Please select an account."
            amount == null -> errorMessage = "Please enter a valid amount."
            else -> {
                val newTransaction = Transaction(
                    id = newId,
                    entry = entry,
                    code = account.code,
                    amount = amount,
                    type = transactionType,
                    date = date
                )
                // Agregar a la lista mutable
                transactions.add(newTransaction)

                // Insertar en la base de datos SQLite
                val rowId = dbHelper.insertTransaction(newTransaction)
                if (rowId == -1L) {
                    errorMessage = "Error inserting transaction into database."
                } else {
                    errorMessage = null
                }
            }
        }
    }


    // UI Layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // State to control the expanded state of the dropdown menu
        var expandedDebit by remember { mutableStateOf(false) }

        // State to hold the selected option
        var selectedOptionDebit by remember { mutableStateOf("Select an account") }

        Text(text = "Welcome, $userName!", style = MaterialTheme.typography.headlineMedium)

        // Title for transaction input section
        Text("Add New Transaction", style = MaterialTheme.typography.titleMedium)

        // Button to trigger the dropdown menu for Debit
        Button(
            onClick = { expandedDebit = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedOptionDebit)
        }

        // DropdownMenu for Debit
        DropdownMenu(
            expanded = expandedDebit,
            onDismissRequest = { expandedDebit = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            accounts.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        selectedAccountDebit = option
                        selectedOptionDebit = option.name
                        expandedDebit = false
                    }
                )
            }
        }

        // Display the selected Debit option below the dropdown
        Text(text = "Selected Debit: ${selectedAccountDebit?.name}")

        // State to control the expanded state of the dropdown menu for Credit
        var expandedCredit by remember { mutableStateOf(false) }

        // State to hold the selected option for Credit
        var selectedOptionCredit by remember { mutableStateOf("Select an account") }

        // Button to trigger the dropdown menu for Credit
        Button(
            onClick = { expandedCredit = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedOptionCredit)
        }

        // DropdownMenu for Credit
        DropdownMenu(
            expanded = expandedCredit,
            onDismissRequest = { expandedCredit = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            accounts.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        selectedAccountCredit = option
                        selectedOptionCredit = option.name
                        expandedCredit = false
                    }
                )
            }
        }

        // Display the selected Credit option below the dropdown
        Text(text = "Selected Credit: ${selectedAccountCredit?.name}")

        // Amount Input Field
        OutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val newEntry = (transactions.maxOfOrNull { it.entry } ?: 0) + 1

        // Button to add Transaction
        Button(
            onClick = {
                dateInput = currentDate
                addTransaction(TransactionType.DEBIT, newEntry)
                addTransaction(TransactionType.CREDIT, newEntry)
                // Reset input fields
                selectedAccountDebit = null
                selectedAccountCredit = null
                selectedOptionDebit = "Select an account"
                selectedOptionCredit = "Select an account"
                amountInput = ""
            },
            enabled = selectedAccountCredit != null && selectedAccountDebit != null && amountInput.isNotBlank()
        ) {
            Text("Add Transaction")
        }

        // Display error message if any
        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
        Button(onClick = onNavigateToTransactions, modifier = Modifier.fillMaxWidth()) {
            Text(text = "View Transactions")
        }

        Button(onClick = onNavigateToAnalysis, modifier = Modifier.fillMaxWidth()) {
            Text(text = "View Analysis")
        }

        Button(onClick = onNavigateToAccounts, modifier = Modifier.fillMaxWidth()) {
            Text(text = "View Accounts")
        }

        // Spacer to push the logout button to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // Logout button at the bottom
        Button(
            onClick = {
                onLogout() // Call the passed logout function
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red, // Set the button background color
                contentColor = Color.White // Set the text color
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Optional: Add padding for better layout
        ) {
            Text(text = "Logout")
        }


    }

}