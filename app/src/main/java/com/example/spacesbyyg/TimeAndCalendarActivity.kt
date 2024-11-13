// TimeAndCalendarActivity.kt
package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Build;
import android.view.WindowManager;
import android.graphics.Color;
import java.text.SimpleDateFormat
import java.util.*

class TimeAndCalendarActivity : AppCompatActivity() {

    private lateinit var selectedDate: String
    private lateinit var selectedTime: String
    private lateinit var selectedRoom: String

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

        setContentView(R.layout.activity_time_selection)

        // Retrieve the selected room from the intent
        selectedRoom = intent.getStringExtra("room") ?: ""

        // Initialize views
        val calendarView: CalendarView = findViewById(R.id.calendarView)
        val timeSlotLayout: LinearLayout = findViewById(R.id.timeSlotLayout)
        val morningSlotButton: Button = findViewById(R.id.morningSlotButton)
        val afternoonSlotButton: Button = findViewById(R.id.afternoonSlotButton)
        val continueButton: Button = findViewById(R.id.continueButton)
        val backToRoomsButton: Button = findViewById(R.id.backToRoomsButton)

        // Disable continue button initially
        continueButton.isEnabled = false

        // Date formatter
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())

        // Handle date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)
            // Show time slots
            timeSlotLayout.visibility = View.VISIBLE
            // Check availability for the selected date
            checkAvailability(selectedDate, morningSlotButton, afternoonSlotButton)
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
            if (this::selectedDate.isInitialized && this::selectedTime.isInitialized) {
                val intent = Intent(this, UserInfoActivity::class.java)
                intent.putExtra("room", selectedRoom)
                intent.putExtra("date", selectedDate)  // Ensure you're using "date" here
                intent.putExtra("time", selectedTime)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_LONG).show()
            }
        }

        // Back to Rooms Button Logic
        backToRoomsButton.setOnClickListener {
            val intent = Intent(this, RoomSelectionActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
    }

    private fun checkAvailability(selectedDate: String, morningSlotButton: Button, afternoonSlotButton: Button) {
        val firestore = FirebaseFirestore.getInstance()
        val bookingsRef = firestore.collection("Bookings")

        // Query Firestore to check if the time slots are booked
        bookingsRef.whereEqualTo("room", selectedRoom)
            .whereEqualTo("date", selectedDate) // Querying with "date"
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
        if (this::selectedDate.isInitialized && this::selectedTime.isInitialized) {
            continueButton.isEnabled = true
            continueButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.lime_green)
        }
    }
}
