package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.util.Log // For logging
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*

class AdminLoginActivity : AppCompatActivity() {

    // Declare FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    // UI Elements
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_admin_login)

        // Initialize UI Elements
        emailEditText = findViewById(R.id.usernameEditText) // Assuming it's for email
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        // Login Button Logic
        loginButton.setOnClickListener {
            val enteredEmail = emailEditText.text.toString().trim()
            val enteredPassword = passwordEditText.text.toString().trim()

            // Validate input fields
            if (!validateInputs(enteredEmail, enteredPassword)) {
                return@setOnClickListener
            }

            // Authenticate using Firebase Authentication
            signIn(enteredEmail, enteredPassword)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        // Reset previous errors
        emailEditText.error = null
        passwordEditText.error = null

        // Check for empty email
        if (email.isEmpty()) {
            emailEditText.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Validate email format
            emailEditText.error = getString(R.string.error_invalid_email_format)
            isValid = false
        }

        // Check for empty password
        if (password.isEmpty()) {
            passwordEditText.error = getString(R.string.error_empty_password)
            isValid = false
        }

        return isValid
    }

    private fun signIn(email: String, password: String) {
        // Disable login button to prevent multiple clicks
        loginButton.isEnabled = false

        // Sign in with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Re-enable login button
                loginButton.isEnabled = true

                if (task.isSuccessful) {
                    // Sign-in success
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // Sign-in failure
                    Log.w(TAG, "signInWithEmail:failure", task.exception)

                    // Provide friendly error messages
                    val exception = task.exception
                    handleSignInError(exception)
                    updateUI(null)
                }
            }
    }

    private fun handleSignInError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> {
                // Email address not found
                emailEditText.error = getString(R.string.error_invalid_credentials)
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Wrong password or invalid email format
                when (exception.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> {
                        passwordEditText.error = getString(R.string.error_invalid_credentials)
                    }
                    "ERROR_INVALID_EMAIL" -> {
                        emailEditText.error = getString(R.string.error_invalid_email_format)
                    }
                    else -> {
                        showGenericAuthError()
                    }
                }
            }
            else -> {
                // Other errors
                showGenericAuthError()
            }
        }
    }

    private fun showGenericAuthError() {
        // Show a generic error message
        val message = getString(R.string.error_authentication_failed)
        emailEditText.error = message
        passwordEditText.error = message
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in
            // Navigate to Admin Portal
            val intent = Intent(this, AdminPortalActivity::class.java)
            startActivity(intent)
            finish() // Close the login activity
        } else {
            // User is signed out or sign-in failed
            // Keep the user on the login screen
            // Optionally, clear the password field
            passwordEditText.text.clear()
        }
    }

    companion object {
        private const val TAG = "AdminLoginActivity"
    }
}
