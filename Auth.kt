// Auth.kt
package com.devssoft.accounting

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Auth(private val context: Context) {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    fun initAuth() {
        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Reference to client ID
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(account: GoogleSignInAccount, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    onSuccess()
                } else {
                    // Sign in failure
                    onFailure(task.exception ?: Exception("Sign in failed"))
                }
            }
    }

    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun signOut(onSuccess: () -> Unit) {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            onSuccess()
        }
    }
}
