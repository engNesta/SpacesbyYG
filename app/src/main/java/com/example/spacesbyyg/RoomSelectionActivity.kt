package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore

class RoomSelectionActivity : AppCompatActivity() {

    private lateinit var selectedRoom: String
    private var roomSelected = false
    private lateinit var firestore: FirebaseFirestore
    private var bookingDocumentId: String? = null // To store booking document ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_selection)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

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
            updateButtonColors(conferenceRoomButton, creativeRoomButton, selectedButtonColor, originalButtonColor)
            continueButton.isEnabled = true // Enable Continue button
        }

        creativeRoomButton.setOnClickListener {
            selectedRoom = "Creative Room"
            roomSelected = true
            updateButtonColors(creativeRoomButton, conferenceRoomButton, selectedButtonColor, originalButtonColor)
            continueButton.isEnabled = true // Enable Continue button
        }

        // Continue Button Logic
        continueButton.setOnClickListener {
            if (roomSelected) {
                // Add the room to Firestore's "Bookings" collection
                addRoomToFirestore(selectedRoom)
            }
        }
    }

    // Function to update button colors
    private fun updateButtonColors(selectedButton: Button, otherButton: Button, selectedColor: Int, originalColor: Int) {
        selectedButton.setBackgroundColor(selectedColor)
        otherButton.setBackgroundColor(originalColor)
    }

    // Function to add the selected room to Firestore
    private fun addRoomToFirestore(room: String) {
        // Create a new booking document with the selected room
        val bookingData = hashMapOf("room" to room)

        // Add the booking data to the "Bookings" collection
        firestore.collection("Bookings")
            .add(bookingData)
            .addOnSuccessListener { documentReference ->
                // Successfully added booking, proceed to next activity
                bookingDocumentId = documentReference.id // Store the document ID
                Toast.makeText(this, "Room selected: $room", Toast.LENGTH_SHORT).show()

                // Pass the booking document ID and selected room to the next activity
                val intent = Intent(this, TimeAndCalendarActivity::class.java)
                intent.putExtra("room", room) // Passing selected room to next activity
                intent.putExtra("bookingId", bookingDocumentId) // Pass the booking document ID
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                // Handle failure
                Toast.makeText(this, "Failed to save booking: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
