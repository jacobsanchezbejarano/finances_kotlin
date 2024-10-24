package com.devssoft.accounting

import android.content.Context

object SessionManager {

    private const val PREF_NAME = "user_session"
    private const val KEY_SIGNED_IN = "is_signed_in"
    private const val KEY_USER_NAME = "user_name"

    // Save user session to SharedPreferences
    fun saveUserSession(context: Context, isSignedIn: Boolean, userName: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_SIGNED_IN, isSignedIn)
        editor.putString(KEY_USER_NAME, userName)
        editor.apply()
    }

    // Load user session from SharedPreferences
    fun loadUserSession(context: Context): Pair<Boolean, String> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isSignedIn = sharedPreferences.getBoolean(KEY_SIGNED_IN, false)
        val userName = sharedPreferences.getString(KEY_USER_NAME, "User") ?: "User"
        return Pair(isSignedIn, userName)
    }
}
