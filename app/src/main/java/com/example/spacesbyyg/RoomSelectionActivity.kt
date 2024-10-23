package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RoomSelectionActivity : AppCompatActivity() {

    private lateinit var selectedRoom: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_selection)

        // UI Elements
        val conferenceRoomButton: Button = findViewById(R.id.conferenceRoomButton)
        val creativeRoomButton: Button = findViewById(R.id.creativeRoomButton)
        val continueButton: Button = findViewById(R.id.continueButton)

        // Room Selection Logic
        conferenceRoomButton.setOnClickListener {
            selectedRoom = "Conference Room"
        }

        creativeRoomButton.setOnClickListener {
            selectedRoom = "Creative Room"
        }

        // Continue Button Logic
        continueButton.setOnClickListener {
            // Pass the selected room to the next activity (Time and Calendar selection)
            val intent = Intent(this, TimeAndCalendarActivity::class.java)
            intent.putExtra("room", selectedRoom) // Passing selected room to next activity
            startActivity(intent)
        }
    }
}
