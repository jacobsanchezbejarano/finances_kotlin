// DatabaseHelper.kt
package com.devssoft.accounting

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "transactions.db"
        const val DATABASE_VERSION = 3 // Increment version for new table structure
        const val TABLE_TRANSACTIONS = "transactions"
        const val TABLE_ACCOUNTS = "accounts" // New table for accounts
        const val COLUMN_ID = "id"
        const val COLUMN_CODE = "code"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_TYPE = "type"
        const val COLUMN_DATE = "date"

        // Columns for accounts
        const val COLUMN_ACCOUNT_NAME = "name"
        const val COLUMN_ACCOUNT_CODE = "code"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create transactions table
        val createTransactionsTable = """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CODE INTEGER NOT NULL,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL
            )
        """.trimIndent()

        // Create accounts table
        val createAccountsTable = """
            CREATE TABLE $TABLE_ACCOUNTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACCOUNT_NAME TEXT NOT NULL,
                $COLUMN_ACCOUNT_CODE INTEGER NOT NULL UNIQUE
            )
        """.trimIndent()

        db.execSQL(createTransactionsTable)
        db.execSQL(createAccountsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNTS")
        onCreate(db)
    }

    // Function to insert a new account into the database
    fun insertAccount(account: Account): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACCOUNT_NAME, account.name)
            put(COLUMN_ACCOUNT_CODE, account.code)
        }

        return db.insert(TABLE_ACCOUNTS, null, values).also {
            db.close()
        }
    }

    // Function to retrieve all accounts
    fun getAllAccounts(): List<Account> {
        val accounts = mutableListOf<Account>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ACCOUNTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ID ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val code = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_CODE))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_NAME))

                accounts.add(Account(id, code, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return accounts
    }

    fun insertTransaction(transaction: Transaction): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CODE, transaction.code)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_TYPE, transaction.type.toString())
            put(COLUMN_DATE, transaction.date)
        }

        return db.insert(TABLE_TRANSACTIONS, null, values).also {
            db.close()
        }
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ID ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val code = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CODE))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val typeStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

                val type = TransactionType.valueOf(typeStr)
                transactions.add(Transaction(id, code, amount, type, date))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return transactions
    }
}
