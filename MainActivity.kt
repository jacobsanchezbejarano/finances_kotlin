// MainActivity.kt
package com.devssoft.accounting

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.devssoft.accounting.Auth
import com.google.firebase.FirebaseApp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.compose.*
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

            var userName by remember { mutableStateOf("") }

            if (authManager.isUserSignedIn()) {
                AccountingApp(userName)
            } else {
                // Show the Google sign-in screen
                SignInScreen(authManager, onSignInSuccess = { account ->
                    // Proceed to the main app if sign-in is successful
                    userName = account.displayName.toString()
                    Log.d("SignIn", "Sign-in successful: ${account.displayName}")
                    setContent { AccountingApp(userName) }
                })
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
