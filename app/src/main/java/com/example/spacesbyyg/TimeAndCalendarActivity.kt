package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TimeAndCalendarActivity : AppCompatActivity() {

    private lateinit var selectedDay: String
    private lateinit var selectedTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_selection)  // Make sure this XML is correct

        // Time Slot Layout (Initially Hidden)
        val timeSlotLayout: LinearLayout = findViewById(R.id.timeSlotLayout)

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

        // Logic for selecting a day
        for (dayButton in dayButtons) {
            dayButton.setOnClickListener {
                selectedDay = dayButton.text.toString()
                timeSlotLayout.visibility = View.VISIBLE // Show time slots
                Toast.makeText(this, "Selected Day: $selectedDay", Toast.LENGTH_SHORT).show()
            }
        }

        // Time Slot Selection Logic
        findViewById<Button>(R.id.morningSlotButton).setOnClickListener {
            selectedTime = "8:00 - 12:00"
            Toast.makeText(this, "Selected Morning Slot", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.afternoonSlotButton).setOnClickListener {
            selectedTime = "13:00 - 18:00"
            Toast.makeText(this, "Selected Afternoon Slot", Toast.LENGTH_SHORT).show()
        }

        // Continue Button Logic
        findViewById<Button>(R.id.continueButton).setOnClickListener {
            if (this::selectedDay.isInitialized && this::selectedTime.isInitialized) {
                // Navigate to UserInfoActivity to collect user information
                val intent = Intent(this, UserInfoActivity::class.java)
                intent.putExtra("room", intent.getStringExtra("room")) // Pass the selected room
                intent.putExtra("day", selectedDay) // Pass the selected day
                intent.putExtra("time", selectedTime) // Pass the selected time
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a day and time", Toast.LENGTH_LONG).show()
            }
        }
    }
}
