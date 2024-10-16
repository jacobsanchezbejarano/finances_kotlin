// FinancialAnalysis.kt
package com.devssoft.accounting

fun analyzeAssetsLiabilitiesAndEquity(
    transactions: List<Transaction>,
    assetsList: List<Account>,
    liabilitiesList: List<Account>,
    equityList: List<Account>
): Triple<Double, Double, Double> {
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
