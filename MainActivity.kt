// MainActivity.kt
package com.devssoft.accounting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.FirebaseApp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class MainActivity : ComponentActivity() {

    private lateinit var authManager: Auth

    companion object {
        private const val SIGN_IN_REQUEST_CODE = 1001 // Unique integer value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        authManager = Auth(this)
        authManager.initAuth()

        setContent {
            // Remember the user's name state
            var userName by remember { mutableStateOf("User") } // Default name

            if (authManager.isUserSignedIn()) {
                // Load the main app if the user is signed in
                userName = authManager.getCurrentUser()?.displayName ?: "User"
            }


            // Call the AccountingApp composable to show the main UI
            if (userName != "User") {
                // Show the main app if the user is signed in and has a valid name
                AccountingApp(userName = userName)
            }else{
                // Show the Google sign-in screen
                SignInScreen(authManager) { account ->
                    // Proceed to the main app if sign-in is successful
                    Log.d("SignIn", "Sign-in successful: ${account.displayName}")
                    // Update the userName with the account's display name
                    userName = account.displayName ?: "User"
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (authManager.handleSignInResult(task)) {
                // Sign-in was successful, update UI
            } else {
                // Sign-in failed, handle error
            }
        }
    }
}


@Composable
fun SignInScreen(authManager: Auth, onSignInSuccess: (GoogleSignInAccount) -> Unit) {
    // Use a launcher for the activity result
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let { onSignInSuccess(it) } // Pass the account back on success
            } catch (e: ApiException) {
                Log.e("SignIn", "Google sign-in failed", e)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val signInIntent = authManager.getSignInIntent()
            signInLauncher.launch(signInIntent) // Launch the sign-in intent
        }) {
            Text("Sign in with Google")
        }
    }
}

