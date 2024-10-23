package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // UI Elements
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        // Login Button Logic
        loginButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            // Validate login credentials against Firestore
            firestore.collection("Admin").document("adminCredentials")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val storedUsername = document.getString("username")
                        val storedPassword = document.getString("password")

                        // Check if entered credentials match the stored ones
                        if (enteredUsername == storedUsername && enteredPassword == storedPassword) {
                            // Navigate to Admin Portal after successful login
                            val intent = Intent(this, AdminPortalActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Show error message if credentials are incorrect
                            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Admin credentials not found", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure in fetching the document
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
