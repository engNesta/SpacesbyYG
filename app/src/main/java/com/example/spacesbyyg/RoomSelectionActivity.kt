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

        // Load colors from resources
        val selectedButtonColor = ContextCompat.getColor(this, R.color.button_enabled)  // Dark Green
        val unselectedButtonColor = ContextCompat.getColor(this, R.color.button_disabled) // Light Green

        // Room Selection Logic
        conferenceRoomButton.setOnClickListener {
            selectedRoom = "Conference Room"
            roomSelected = true
            updateButtonColors(
                conferenceRoomButton,
                creativeRoomButton,
                selectedButtonColor,
                unselectedButtonColor
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
                unselectedButtonColor
            )
            continueButton.isEnabled = true // Enable Continue button
        }

        // Continue Button Logic
        continueButton.setOnClickListener {
            if (roomSelected) {
                val intent = Intent(this, TimeAndCalendarActivity::class.java)
                intent.putExtra("room", selectedRoom)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Vänligen välj ett rum", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to update button colors
    private fun updateButtonColors(
        selectedButton: Button,
        otherButton: Button,
        selectedColor: Int,
        unselectedColor: Int
    ) {
        selectedButton.setBackgroundColor(selectedColor) // Dark Green for selected button
        otherButton.setBackgroundColor(unselectedColor)  // Light Green for unselected button
    }
}
