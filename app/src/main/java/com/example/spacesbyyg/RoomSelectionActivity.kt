// RoomSelectionActivity.kt
package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class RoomSelectionActivity : AppCompatActivity() {

    private lateinit var selectedRoom: String
    private var roomSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
