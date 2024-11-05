package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.graphics.Color;


class AdminLoginActivity : AppCompatActivity() {

    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    // UI Elements
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var showPasswordCheckBox: CheckBox
    private lateinit var loginButton: Button

    companion object {
        private const val TAG = "AdminLoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            // Clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // Add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // Set the status bar to transparent
            window.statusBarColor = Color.TRANSPARENT

            // Make the content appear behind the status bar
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setContentView(R.layout.activity_admin_login)

        // Initialize UI Elements
        emailEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox)
        loginButton = findViewById(R.id.loginButton)

        // Show Password CheckBox Logic
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Show password
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Hide password
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Move the cursor to the end of the text
            passwordEditText.setSelection(passwordEditText.text.length)
        }

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

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        // Reset previous errors
        emailEditText.error = null
        passwordEditText.error = null

        // Check for empty email
        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Validate email format
            emailEditText.error = "Invalid email format"
            isValid = false
        }

        // Check for empty password
        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
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
                emailEditText.error = "Invalid email or password"
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Wrong password or invalid email format
                when (exception.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> {
                        passwordEditText.error = "Invalid email or password"
                    }
                    "ERROR_INVALID_EMAIL" -> {
                        emailEditText.error = "Invalid email format"
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
        val message = "Authentication failed. Please try again."
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
}
