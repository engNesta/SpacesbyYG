package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UserInfoActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Retrieve data from the previous activity (room, day, and time)
        val selectedRoom = intent.getStringExtra("room")
        val selectedDay = intent.getStringExtra("day")
        val selectedTime = intent.getStringExtra("time")

        // UI Elements
        val nameField: EditText = findViewById(R.id.userName)
        val surnameField: EditText = findViewById(R.id.userSurname)
        val companyField: EditText = findViewById(R.id.userCompany)
        val emailField: EditText = findViewById(R.id.userEmail)
        val phoneField: EditText = findViewById(R.id.userPhone)
        val submitButton: Button = findViewById(R.id.submitBookingButton)

        // Handle the submission of booking information
        submitButton.setOnClickListener {
            val userName = nameField.text.toString()
            val userSurname = surnameField.text.toString()
            val userEmail = emailField.text.toString()
            val userPhone = phoneField.text.toString()
            val userCompany = companyField.text.toString()

            if (userName.isNotEmpty() && userSurname.isNotEmpty() && userEmail.isNotEmpty() && userPhone.isNotEmpty()) {
                // Create a booking document to store in Firestore
                val bookingData = hashMapOf(
                    "room" to selectedRoom,
                    "day" to selectedDay,
                    "time" to selectedTime,
                    "userName" to userName,
                    "userSurname" to userSurname,
                    "userEmail" to userEmail,
                    "userPhone" to userPhone,
                    "userCompany" to userCompany
                )

                // Store booking information in Firestore under the "Bookings" collection
                firestore.collection("Bookings")
                    .add(bookingData)
                    .addOnSuccessListener {
                        // Booking saved, navigate to thank you page
                        val intent = Intent(this, ThankYouActivity::class.java)
                        intent.putExtra("name", userName) // Pass user name to thank-you page
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to submit booking: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_LONG).show()
            }
        }
    }
}
