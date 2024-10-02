package com.devssoft.accounting

import android.os.Bundle
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.devssoft.accounting.ui.theme.AccountingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

        }
    }
}

data class Account(val code: Int, val name: String)

enum class TransactionType {
    DEBIT, CREDIT
}

data class Transaction(
    val id: Int,
    val code: Int,
    val amount: Double,
    val type: TransactionType,
    val date: String
)

val testTransactions = listOf(
    Transaction(id = 1, code = 10001, amount = 2000.00, type = TransactionType.DEBIT, date = "2024-04-01 10:15:30"),
    Transaction(id = 2, code = 40001, amount = 2000.00, type = TransactionType.CREDIT, date = "2024-04-01 10:15:30"),
    Transaction(id = 3, code = 60001, amount = 700.00, type = TransactionType.DEBIT, date = "2024-04-03 09:45:00"),
    Transaction(id = 4, code = 10001, amount = 700.00, type = TransactionType.CREDIT, date = "2024-04-03 09:45:00"),
    Transaction(id = 5, code = 10001, amount = 320.00, type = TransactionType.DEBIT, date = "2024-04-05 11:10:15"),
    Transaction(id = 6, code = 30001, amount = 320.00, type = TransactionType.CREDIT, date = "2024-04-05 11:10:15"),
    Transaction(id = 7, code = 10001, amount = 450.25, type = TransactionType.DEBIT, date = "2024-04-07 13:50:30"),
    Transaction(id = 8, code = 20001, amount = 450.25, type = TransactionType.CREDIT, date = "2024-04-07 13:50:30"),
    Transaction(id = 9, code = 10001, amount = 980.40, type = TransactionType.DEBIT, date = "2024-04-09 12:40:10"),
    Transaction(id = 10, code = 40001, amount = 980.40, type = TransactionType.CREDIT, date = "2024-04-09 12:40:10")
)

fun analyzeAssetsLiabilitiesAndEquity(transactions: List<Transaction>, assetsList: List<Account>, liabilitiesList: List<Account>, equityList: List<Account>): Triple<Double, Double, Double> {
    var totalAssets = 0.0
    var totalLiabilities = 0.0
    var totalEquity = 0.0

    transactions.forEach { transaction ->
        when (transaction.code) {
            in assetsList.map { it.code } -> {
                if (transaction.type == TransactionType.DEBIT) {
                    totalAssets += transaction.amount
                } else {
                    totalAssets -= transaction.amount
                }
            }
            in liabilitiesList.map { it.code } -> {
                if (transaction.type == TransactionType.CREDIT) {
                    totalLiabilities += transaction.amount
                }
            }
            in equityList.map { it.code } -> {
                if (transaction.type == TransactionType.CREDIT) {
                    totalEquity += transaction.amount
                }
            }
        }
    }

    return Triple(totalAssets, totalLiabilities, totalEquity)
}

fun calculateEarningsAndLosses(
    transactions: List<Transaction>,
    incomeList: List<Account>,
    costsList: List<Account>,
    expensesList: List<Account>
): Double {
    var totalIncome = 0.0
    var totalCosts = 0.0
    var totalExpenses = 0.0

    val incomeCodes = incomeList.map { it.code }.toSet()
    val costsCodes = costsList.map { it.code }.toSet()
    val expensesCodes = expensesList.map { it.code }.toSet()

    transactions.forEach { transaction ->
        when {
            transaction.code in incomeCodes -> {
                when (transaction.type) {
                    TransactionType.CREDIT -> totalIncome += transaction.amount
                    TransactionType.DEBIT -> totalIncome -= transaction.amount
                }
            }
            transaction.code in costsCodes -> {
                when (transaction.type) {
                    TransactionType.DEBIT -> totalCosts += transaction.amount
                    TransactionType.CREDIT -> totalCosts -= transaction.amount
                }
            }
            transaction.code in expensesCodes -> {
                when (transaction.type) {
                    TransactionType.DEBIT -> totalExpenses += transaction.amount
                    TransactionType.CREDIT -> totalExpenses -= transaction.amount
                }
            }
        }
    }

    return totalIncome - (totalCosts + totalExpenses)
}



@Composable
fun ShowAccounts(accounts: List<Account>, modifier: Modifier = Modifier) {

    var assetsList by remember { mutableStateOf(listOf<Account>()) }
    var liabilitiesList by remember { mutableStateOf(listOf<Account>()) }
    var equityList by remember { mutableStateOf(listOf<Account>()) }
    var incomeList by remember { mutableStateOf(listOf<Account>()) }
    var costsList by remember { mutableStateOf(listOf<Account>()) }
    var expensesList by remember { mutableStateOf(listOf<Account>()) }

    LaunchedEffect(accounts) {
        accounts.forEach { account ->
            when (account.code) {
                in 10001..19999 -> assetsList = assetsList + account
                in 20001..29999 -> liabilitiesList = liabilitiesList + account
                in 30001..39999 -> equityList = equityList + account
                in 40001..49999 -> incomeList = incomeList + account
                in 50001..59999 -> costsList = costsList + account
                in 60001..69999 -> expensesList = expensesList + account
            }
        }
    }

    Column(modifier = modifier) {
        Text("Assets: ${assetsList.joinToString { it.name }}")
        Text("Liabilities: ${liabilitiesList.joinToString { it.name }}")
        Text("Equity: ${equityList.joinToString { it.name }}")
        Text("Income: ${incomeList.joinToString { it.name }}")
        Text("Costs: ${costsList.joinToString { it.name }}")
        Text("Expenses: ${expensesList.joinToString { it.name }}")
    }
}

@Composable
fun getPermutableAccounts(
    account: Account,
    assetsList: List<Account>,
    liabilitiesList: List<Account>,
    equityList: List<Account>,
    incomeList: List<Account>,
    costsList: List<Account>,
    expensesList: List<Account>
): List<Account> {
    return when (account.code) {
        in 10001..19999 -> assetsList + liabilitiesList + equityList + incomeList
        in 20001..29999 -> assetsList + expensesList
        in 30001..39999 -> assetsList
        in 40001..49999 -> assetsList
        in 50001..59999 -> assetsList
        in 60001..69999 -> assetsList + liabilitiesList
        else -> listOf()
    }
}

@Preview(showBackground = true)
@Composable
fun AccountsPreview() {
    AccountingTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val accounts = listOf(
                Account(10001, "Cash"),
                Account(20001, "Accounts Payable"),
                Account(30001, "Owner's Equity"),
                Account(40001, "Sales Revenue"),
                Account(50001, "Cost of Goods Sold"),
                Account(60001, "Rent Expense")
            )


            ShowAccounts(
                accounts = accounts,
                modifier = Modifier.padding(innerPadding)
            )

            val assetsList = accounts.filter { it.code in 10001..19999 }
            val liabilitiesList = accounts.filter { it.code in 20001..29999 }
            val equityList = accounts.filter { it.code in 30001..39999 }
            val incomeList = accounts.filter { it.code in 40001..49999 }
            val costsList = accounts.filter { it.code in 50001..59999 }
            val expensesList = accounts.filter { it.code in 60001..69999 }



            // Statements

            val netIncome = calculateEarningsAndLosses(
                transactions = testTransactions,
                incomeList,
                costsList,
                expensesList
            )

            val (totalAssets, totalLiabilities, totalEquity) = analyzeAssetsLiabilitiesAndEquity(
                transactions = testTransactions,
                assetsList,
                liabilitiesList,
                equityList
            )



            // Filter

            val cash = Account(15000, "Cash")


            var permutableAccountsText by remember { mutableStateOf("") }
            val permutableAccounts = getPermutableAccounts(
                cash,
                assetsList,
                liabilitiesList,
                equityList,
                incomeList,
                costsList,
                expensesList
            )
            permutableAccountsText = "Permutable accounts for ${cash.name}: ${permutableAccounts.joinToString { it.name }}"

            Column(modifier = Modifier.padding(innerPadding)) {
                ShowAccounts(
                    accounts = accounts,
                    modifier = Modifier.padding()
                )


                Text(text = "-------------------------", modifier = Modifier.padding())

                Text(text = "Net Income: ${netIncome}")

                Text(text = "Assets: ${ totalAssets }")
                Text(text = "Liabilities: ${ totalLiabilities }")
                Text(text = "Equity: ${ totalEquity + netIncome }")

                Text(text = "-------------------------", modifier = Modifier.padding())


                Text(text = permutableAccountsText)

            }
        }
    }
}
