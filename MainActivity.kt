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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

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
            // State variable to track if the user is signed in
            var isSignedIn by remember { mutableStateOf(authManager.isUserSignedIn()) }
            var userName by remember { mutableStateOf("User") }

            if (authManager.isUserSignedIn()) {
                // Load the main app if the user is signed in
                userName = authManager.getCurrentUser()?.displayName ?: "User"
            }
            if (isSignedIn) {
                // Show the main app if the user is signed in and has a valid name
                AccountingApp(userName = userName) {
                    authManager.signOut()
                    isSignedIn = false
                    userName = "User" // Reset the user name on logout
                }

            } else {
                // Show the Google sign-in screen
                SignInScreen(authManager) { account ->
                    // Proceed to the main app if sign-in is successful
                    Log.d("SignIn", "Sign-in successful: ${account.displayName}")
                    // Update the userName with the account's display name
                    userName = account.displayName ?: "User"
                    isSignedIn = true // Update the state to reflect the user is signed in
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
        GoogleSignInButton(onClick = {
            val signInIntent = authManager.getSignInIntent()
            signInLauncher.launch(signInIntent) // Launch the sign-in intent
        })

    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    // Google Colors
    val googleBlue = Color(0xFF4285F4)
    val googleRed = Color(0xFFDB4437)
    val googleYellow = Color(0xFFF4B400)
    val googleGreen = Color(0xFF0F9D58)

    // Button with Google logo
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        // Google Logo
        Image(
            painter = painterResource(id = R.drawable.ic_google), // Ensure you have a Google logo drawable in your resources
            contentDescription = "Google Logo",
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )

        // Google Sign-In Text
        Text(
            text = "Sign in with Google",
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
