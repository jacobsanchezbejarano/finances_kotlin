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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analysis") },
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
        AnalysisList(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AnalysisList(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val context = LocalContext.current
        val dbHelper = remember { DatabaseHelper(context) }

        val initialTransactions = remember { dbHelper.getAllTransactions() }
        val transactions = remember { mutableStateListOf<Transaction>().apply { addAll(initialTransactions) } }
        val accounts = remember { mutableStateListOf<Account>().apply { addAll(dbHelper.getAllAccounts()) } }

        val assetsList = accounts.filter { it.code in 10001..19999 }
        val liabilitiesList = accounts.filter { it.code in 20001..29999 }
        val equityList = accounts.filter { it.code in 30001..39999 }
        val incomeList = accounts.filter { it.code in 40001..49999 }
        val costsList = accounts.filter { it.code in 50001..59999 }
        val expensesList = accounts.filter { it.code in 60001..69999 }

        val (totalAssets, totalLiabilities, totalEquity) = analyzeAssetsLiabilitiesAndEquity(
            transactions = transactions,
            assetsList = assetsList,
            liabilitiesList = liabilitiesList,
            equityList = equityList
        )

        val netIncome = calculateEarningsAndLosses(
            transactions = transactions,
            incomeList = incomeList,
            costsList = costsList,
            expensesList = expensesList
        )

        // Display financial statements
        Text("Net Income: $netIncome")
        Text("Assets: $totalAssets")
        Text("Liabilities: $totalLiabilities")
        Text("Equity: ${totalEquity + netIncome}")
    }
}
