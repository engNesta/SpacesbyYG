// RoomSelectionActivity.kt
package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.graphics.Color;

class RoomSelectionActivity : AppCompatActivity() {

    private lateinit var selectedRoom: String
    private var roomSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        setContentView(R.layout.activity_room_selection)

        // UI Elements
        val conferenceRoomButton: Button = findViewById(R.id.conferenceRoomButton)
        val creativeRoomButton: Button = findViewById(R.id.creativeRoomButton)
        val continueButton: Button = findViewById(R.id.continueButton)

        // Disable Continue button initially
        continueButton.isEnabled = false

        // Original button colors
        val originalButtonColor = ContextCompat.getColor(this, android.R.color.darker_gray)
        val selectedButtonColor = ContextCompat.getColor(this, android.R.color.holo_blue_light)

        // Room Selection Logic
        conferenceRoomButton.setOnClickListener {
            selectedRoom = "Conference Room"
            roomSelected = true
            updateButtonColors(
                conferenceRoomButton,
                creativeRoomButton,
                selectedButtonColor,
                originalButtonColor
            )
            continueButton.isEnabled = true // Enable Continue button
        }

        creativeRoomButton.setOnClickListener {
            selectedRoom = "Creative Room"
            roomSelected = true
            updateButtonColors(
                creativeRoomButton,
                conferenceRoomButton,
                selectedButtonColor,
                originalButtonColor
            )
            continueButton.isEnabled = true // Enable Continue button
        }

        // Continue Button Logic
        continueButton.setOnClickListener {
            if (roomSelected) {
                // Pass the selected room to the next activity
                val intent = Intent(this, TimeAndCalendarActivity::class.java)
                intent.putExtra("room", selectedRoom) // Passing selected room to next activity
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a room", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to update button colors
    private fun updateButtonColors(
        selectedButton: Button,
        otherButton: Button,
        selectedColor: Int,
        originalColor: Int
    ) {
        selectedButton.setBackgroundColor(selectedColor)
        otherButton.setBackgroundColor(originalColor)
    }
}
