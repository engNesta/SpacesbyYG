package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Make sure this matches the new XML layout

        // Get references to the buttons
        val bookTimeButton: Button = findViewById(R.id.bookTimeButton)
        val adminPortalButton: Button = findViewById(R.id.adminPortalButton)

        // Navigate to Room Selection when "Book Time" is pressed
        bookTimeButton.setOnClickListener {
            val intent = Intent(this, RoomSelectionActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Admin Portal (placeholder for now)
        adminPortalButton.setOnClickListener {
            // You can add admin logic here later
            val intent = Intent(this, AdminLoginActivity::class.java)  // Placeholder, create this activity later
            startActivity(intent)
        }
    }
}
