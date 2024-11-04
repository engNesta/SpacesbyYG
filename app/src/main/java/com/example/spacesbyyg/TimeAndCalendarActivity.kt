// TimeAndCalendarActivity.kt
package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore

class TimeAndCalendarActivity : AppCompatActivity() {

    private lateinit var selectedDay: String
    private lateinit var selectedTime: String
    private lateinit var selectedRoom: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_selection)

        // Retrieve the selected room from the intent
        selectedRoom = intent.getStringExtra("room") ?: ""

        // Time Slot Layout (Initially Hidden)
        val timeSlotLayout: LinearLayout = findViewById(R.id.timeSlotLayout)
        val morningSlotButton: Button = findViewById(R.id.morningSlotButton)
        val afternoonSlotButton: Button = findViewById(R.id.afternoonSlotButton)
        val continueButton: Button = findViewById(R.id.continueButton)
        val backToRoomsButton: Button = findViewById(R.id.backToRoomsButton)

        // Disable continue button initially
        continueButton.isEnabled = false

        // Day Selection Logic: Update IDs based on your XML
        val dayButtons = listOf(
            findViewById<Button>(R.id.sundayButton),
            findViewById<Button>(R.id.mondayButton),
            findViewById<Button>(R.id.tuesdayButton),
            findViewById<Button>(R.id.wednesdayButton),
            findViewById<Button>(R.id.thursdayButton),
            findViewById<Button>(R.id.fridayButton),
            findViewById<Button>(R.id.saturdayButton)
        )

        // Logic for selecting a day and querying Firestore for availability
        for (dayButton in dayButtons) {
            dayButton.setOnClickListener {
                selectedDay = dayButton.text.toString()
                timeSlotLayout.visibility = View.VISIBLE // Show time slots
                checkAvailability(selectedDay, morningSlotButton, afternoonSlotButton)
                Toast.makeText(this, "Selected Day: $selectedDay", Toast.LENGTH_SHORT).show()
            }
        }

        // Time Slot Selection Logic
        morningSlotButton.setOnClickListener {
            selectedTime = "8:00 - 12:00"
            enableContinueButtonIfReady(continueButton)
            Toast.makeText(this, "Selected Morning Slot", Toast.LENGTH_SHORT).show()
        }

        afternoonSlotButton.setOnClickListener {
            selectedTime = "13:00 - 18:00"
            enableContinueButtonIfReady(continueButton)
            Toast.makeText(this, "Selected Afternoon Slot", Toast.LENGTH_SHORT).show()
        }

        // Continue Button Logic
        continueButton.setOnClickListener {
            if (this::selectedDay.isInitialized && this::selectedTime.isInitialized) {
                // Navigate to UserInfoActivity to collect user information
                val intent = Intent(this, UserInfoActivity::class.java)
                intent.putExtra("room", selectedRoom) // Pass the selected room
                intent.putExtra("day", selectedDay) // Pass the selected day
                intent.putExtra("time", selectedTime) // Pass the selected time
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a day and time", Toast.LENGTH_LONG).show()
            }
        }

        // Back to Rooms Button Logic
        backToRoomsButton.setOnClickListener {
            val intent = Intent(this, RoomSelectionActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
    }

    private fun checkAvailability(selectedDay: String, morningSlotButton: Button, afternoonSlotButton: Button) {
        val firestore = FirebaseFirestore.getInstance()
        val bookingsRef = firestore.collection("Bookings")

        // Query Firestore to check if the time slots are booked
        bookingsRef.whereEqualTo("room", selectedRoom)
            .whereEqualTo("day", selectedDay)
            .get()
            .addOnSuccessListener { documents ->
                var morningSlotBooked = false
                var afternoonSlotBooked = false

                for (document in documents) {
                    val bookedTime = document.getString("time")
                    if (bookedTime == "8:00 - 12:00") {
                        morningSlotBooked = true
                    } else if (bookedTime == "13:00 - 18:00") {
                        afternoonSlotBooked = true
                    }
                }

                // Update button states based on availability
                updateSlotButtonState(morningSlotButton, morningSlotBooked)
                updateSlotButtonState(afternoonSlotButton, afternoonSlotBooked)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch availability", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateSlotButtonState(slotButton: Button, isBooked: Boolean) {
        if (isBooked) {
            slotButton.isEnabled = false
            slotButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        } else {
            slotButton.isEnabled = true
            slotButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light))
        }
    }

    private fun enableContinueButtonIfReady(continueButton: Button) {
        if (this::selectedDay.isInitialized && this::selectedTime.isInitialized) {
            continueButton.isEnabled = true
        }
    }
}
