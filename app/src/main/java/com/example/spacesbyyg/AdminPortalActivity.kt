package com.example.spacesbyyg

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminPortalActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var bookingListView: ListView
    private val bookingsList = mutableListOf<String>()
    private val bookingIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_portal)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // UI Elements
        bookingListView = findViewById(R.id.bookingListView)

        // Fetch booking requests from Firestore
        fetchBookingRequests()

        // Handle list item click
        bookingListView.setOnItemClickListener { _, _, position, _ ->
            val selectedBookingId = bookingIds[position]
            showActionDialog(selectedBookingId)
        }
    }

    private fun fetchBookingRequests() {
        firestore.collection("Bookings")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val userName = document.getString("userName")
                    val room = document.getString("room")
                    val day = document.getString("day")
                    val time = document.getString("time")
                    bookingsList.add("$userName - $room - $day $time")
                    bookingIds.add(document.id)
                }

                // Bind the data to ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bookingsList)
                bookingListView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load bookings: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showActionDialog(bookingId: String) {
        val options = arrayOf("Accept & Confirm", "Reject")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an action")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> updateBookingStatus(bookingId, "confirmed")
                1 -> updateBookingStatus(bookingId, "rejected")
            }
        }
        builder.show()
    }

    private fun updateBookingStatus(bookingId: String, status: String) {
        firestore.collection("Bookings").document(bookingId)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking $status successfully", Toast.LENGTH_LONG).show()

                // Send confirmation email
                sendConfirmationEmail(bookingId, status)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update booking: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun sendConfirmationEmail(bookingId: String, status: String) {
        firestore.collection("Bookings").document(bookingId)
            .get()
            .addOnSuccessListener { document ->
                val userEmail = document.getString("userEmail")

                if (userEmail != null) {
                    val subject = "Booking $status"
                    val message = if (status == "confirmed") {
                        "Your booking has been confirmed!"
                    } else {
                        "Your booking has been rejected."
                    }

                    // Call a method to send email (using an external API or email service)
                    sendEmail(userEmail, subject, message)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send email: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun sendEmail(email: String, subject: String, message: String) {
        // Implement email sending logic using an external API
        // Example: You could use an API like SendGrid or SMTP server
        // This is a placeholder for the email sending logic
        Toast.makeText(this, "Email sent to $email", Toast.LENGTH_LONG).show()
    }
}
