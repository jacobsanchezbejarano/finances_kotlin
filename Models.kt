// Models.kt
package com.devssoft.accounting

data class Account(
    val id: Int,
    val code: Int,
    val name: String
)


enum class TransactionType {
    DEBIT, CREDIT
}

data class Transaction(
    val id: Int,
    val entry: Int,
    val code: Int,
    val amount: Double,
    val type: TransactionType,
    val date: String
)
