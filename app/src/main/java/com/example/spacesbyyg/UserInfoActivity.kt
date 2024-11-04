// UserInfoActivity.kt
package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserInfoActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var selectedRoom: String
    private lateinit var selectedDay: String
    private lateinit var selectedTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Retrieve data from the previous activity (room, day, and time)
        selectedRoom = intent.getStringExtra("room") ?: ""
        selectedDay = intent.getStringExtra("day") ?: ""
        selectedTime = intent.getStringExtra("time") ?: ""

        // UI Elements
        val nameField: EditText = findViewById(R.id.userName)
        val surnameField: EditText = findViewById(R.id.userSurname)
        val companyField: EditText = findViewById(R.id.userCompany)
        val emailField: EditText = findViewById(R.id.userEmail)
        val phoneField: EditText = findViewById(R.id.userPhone)
        val submitButton: Button = findViewById(R.id.submitBookingButton)
        val backButton: Button = findViewById(R.id.backToTimeCalendarButton)

        // Handle the submission of booking information
        submitButton.setOnClickListener {
            val userName = nameField.text.toString().trim()
            val userSurname = surnameField.text.toString().trim()
            val userEmail = emailField.text.toString().trim()
            val userPhone = phoneField.text.toString().trim()
            val userCompany = companyField.text.toString().trim()

            // Validate input fields
            if (!validateInputs(userName, userSurname, userEmail, userPhone)) {
                return@setOnClickListener
            }

            // Submit the booking
            submitBooking(
                userEmail,
                userPhone,
                selectedRoom,
                selectedDay,
                selectedTime,
                userName,
                userSurname,
                userCompany
            )
        }

        // Back to Time and Calendar button logic
        backButton.setOnClickListener {
            val intent = Intent(this, TimeAndCalendarActivity::class.java)
            intent.putExtra("room", selectedRoom) // Keep the selected room
            startActivity(intent) // Go back to the Time and Calendar selection
            finish()
        }
    }

    // Input validation function
    private fun validateInputs(name: String, surname: String, email: String, phone: String): Boolean {
        var isValid = true

        // Validate Name
        if (name.isEmpty()) {
            findViewById<EditText>(R.id.userName).error = "Name is required"
            isValid = false
        }

        // Validate Surname
        if (surname.isEmpty()) {
            findViewById<EditText>(R.id.userSurname).error = "Surname is required"
            isValid = false
        }

        // Validate Email
        if (email.isEmpty()) {
            findViewById<EditText>(R.id.userEmail).error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            findViewById<EditText>(R.id.userEmail).error = "Invalid email format"
            isValid = false
        }

        // Validate Phone (Swedish format)
        if (phone.isEmpty()) {
            findViewById<EditText>(R.id.userPhone).error = "Phone number is required"
            isValid = false
        } else if (!Patterns.PHONE.matcher(phone).matches() || !phone.startsWith("+46")) {
            findViewById<EditText>(R.id.userPhone).error = "Invalid Swedish phone number format"
            isValid = false
        }

        return isValid
    }

    // Submit booking to Firestore
    private fun submitBooking(
        email: String,
        phone: String,
        room: String?,
        day: String?,
        time: String?,
        name: String,
        surname: String,
        company: String
    ) {
        val bookingData = hashMapOf(
            "room" to room,
            "day" to day,
            "time" to time,
            "userName" to name,
            "userSurname" to surname,
            "userEmail" to email,
            "userPhone" to phone,
            "userCompany" to company,
            "createdAt" to FieldValue.serverTimestamp(), // Add this line
            "status" to "pending" // Initialize status as pending
        )

        // Store booking information in Firestore
        firestore.collection("Bookings")
            .add(bookingData)
            .addOnSuccessListener {
                // Booking saved, navigate to thank you page
                val intent = Intent(this, ThankYouActivity::class.java)
                intent.putExtra("name", name) // Pass user name to thank-you page
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit booking: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
