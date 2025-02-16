package com.example.spacesbyyg

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.graphics.Color
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
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


        // Override CalendarView selection color
        try {
            val field = CalendarView::class.java.getDeclaredField("mDaySelectorColor")
            field.isAccessible = true
            field.set(calendarView, ContextCompat.getColor(this, R.color.button_green))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Disable continue button initially
        continueButton.isEnabled = false

        // Date formatter
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())

        // Handle date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val selectedCalendarTime = calendar.time
            selectedDate = dateFormat.format(selectedCalendarTime)

            // Get today's date (with time truncated to midnight)
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            // Check if the selected date is before today
            if (calendar.before(today)) {
                // The selected date is in the past, no bookings allowed
                timeSlotLayout.visibility = View.GONE
                continueButton.visibility = View.GONE
                Toast.makeText(this, "Kan inte boka i det förflutna.", Toast.LENGTH_SHORT).show()
                return@setOnDateChangeListener
            }

            // Reset selectedTime and disable continue button whenever date changes
            if (this::selectedTime.isInitialized) {
                selectedTime = ""
            }
            continueButton.isEnabled = false
            continueButton.backgroundTintList =
                ContextCompat.getColorStateList(this, android.R.color.darker_gray)

            // Check if the selected day is Sunday (no bookings allowed)
            if (dayOfWeek == Calendar.SUNDAY) {
                timeSlotLayout.visibility = View.GONE
                continueButton.visibility = View.GONE
                Toast.makeText(this, "Bokningar är inte tillåtna på helger.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnDateChangeListener
            }

            // If it's a future weekday, proceed with showing and checking availability
            timeSlotLayout.visibility = View.VISIBLE
            continueButton.visibility = View.VISIBLE
            checkAvailability(selectedDate, morningSlotButton, afternoonSlotButton, continueButton)
        }

        // Time Slot Selection Logic
        morningSlotButton.setOnClickListener {
            selectedTime = "8:00 - 12:00"
            enableContinueButtonIfReady(continueButton)
            Toast.makeText(this, "Vald morgonsplats", Toast.LENGTH_SHORT).show()
        }

        afternoonSlotButton.setOnClickListener {
            selectedTime = "13:00 - 18:00"
            enableContinueButtonIfReady(continueButton)
            Toast.makeText(this, "Vald eftermiddagsplats", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Välj datum och tid", Toast.LENGTH_LONG).show()
            }
        }

        // Back to Rooms Button Logic
        backToRoomsButton.setOnClickListener {
            val intent = Intent(this, RoomSelectionActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
    }

    private fun checkAvailability(
        selectedDate: String,
        morningSlotButton: Button,
        afternoonSlotButton: Button,
        continueButton: Button
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val bookingsRef = firestore.collection("Bookings")

        // Query Firestore to check if the time slots are booked
        bookingsRef.whereEqualTo("room", selectedRoom)
            .whereEqualTo("date", selectedDate)
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

                // After checking availability, apply time-based logic if date is today
                applyTimeLogicIfToday(selectedDate, morningSlotButton, afternoonSlotButton)

                // Setup click listeners for the buttons
                setupTimeSlotButtonListeners(morningSlotButton, afternoonSlotButton, continueButton)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Det gick inte att hämta tillgänglighet", Toast.LENGTH_SHORT).show()
            }
    }

    private fun applyTimeLogicIfToday(
        selectedDate: String,
        morningSlotButton: Button,
        afternoonSlotButton: Button
    ) {
        // Check if the selected date is today's date
        val sdf = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        val selectedCalendarDate = sdf.parse(selectedDate)
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        if (selectedCalendarDate != null) {
            val selectedCal = Calendar.getInstance()
            selectedCal.time = selectedCalendarDate

            // If selected date is the same as today, apply time checks
            if (selectedCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                selectedCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            ) {

                // Current time
                val currentTime = Calendar.getInstance()
                val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)

                // Morning slot: 8:00 - 12:00
                if (morningSlotButton.isEnabled) {
                    when {
                        currentHour >= 12 -> {
                            // Past 12:00, disable morning slot
                            morningSlotButton.isEnabled = false
                            morningSlotButton.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.button_disabled)
                        }

                        currentHour in 8..11 -> {
                            // Between 8:00 and 12:00
                            val remainingHours = 11 - currentHour // approximate until 12:00
                            Toast.makeText(
                                this,
                                "Skynda! Cirka $remainingHours timme(r) kvar till morgonsplats.",
                                Toast.LENGTH_LONG
                            ).show()

                            // Ensure the button stays green if still enabled
                            morningSlotButton.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.button_green)
                        }
                    }
                }


                // Afternoon slot: 13:00 - 18:00
                if (afternoonSlotButton.isEnabled) {
                    when {
                        currentHour >= 18 -> {
                            // Past 18:00, disable afternoon slot and set to gray
                            afternoonSlotButton.isEnabled = false
                            afternoonSlotButton.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.button_disabled)
                        }

                        currentHour in 13..17 -> {
                            // Between 13:00 and 18:00, keep it green
                            afternoonSlotButton.backgroundTintList =
                                ContextCompat.getColorStateList(this, R.color.button_green)
                            val remainingHours = 17 - currentHour // approximate until 18:00
                            Toast.makeText(
                                this,
                                "Skynda! Cirka $remainingHours timme(r) kvar till eftermiddagsplats.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

            }
        }
    }

    private fun updateSlotButtonState(slotButton: Button, isBooked: Boolean) {
        if (isBooked) {
            slotButton.isEnabled = false
            slotButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_disabled))
        } else {
            slotButton.isEnabled = true
            slotButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_green))
        }
    }

    private fun enableContinueButtonIfReady(continueButton: Button) {
        if (this::selectedDate.isInitialized && this::selectedTime.isInitialized) {
            continueButton.isEnabled = true
            continueButton.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.button_green)
        }
    }

    private fun setupTimeSlotButtonListeners(
        morningSlotButton: Button,
        afternoonSlotButton: Button,
        continueButton: Button
    ) {
        // Set click listeners for both buttons
        morningSlotButton.setOnClickListener {
            selectTimeSlot(morningSlotButton, afternoonSlotButton, "8:00 - 12:00", continueButton)
        }

        afternoonSlotButton.setOnClickListener {
            selectTimeSlot(afternoonSlotButton, morningSlotButton, "13:00 - 18:00", continueButton)
        }
    }

    /**
     * Selects the clicked time slot, disables the other, and enables the continue button.
     *
     * @param selectedButton The button that was clicked.
     * @param otherButton The button that should be disabled.
     * @param time The selected time slot.
     * @param continueButton The continue button that should be enabled when a selection is made.
     */
    private fun selectTimeSlot(
        selectedButton: Button,
        otherButton: Button,
        time: String,
        continueButton: Button
    ) {
        // Change selected button appearance and state
        selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_enabled))
        selectedButton.isSelected = true
        selectedTime = time

        // Re-enable and reset the other button
        otherButton.isEnabled = true
        otherButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_green))
        otherButton.isSelected = true

        // Disable the other button to prevent selection while one is active
        otherButton.isEnabled = true
        otherButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_disabled))

        // Enable continue button if a time slot is selected
        enableContinueButtonIfReady(continueButton)
    }
}