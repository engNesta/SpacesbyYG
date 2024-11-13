// AdminPortalActivity.kt
package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminPortalActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var signOutButton: Button
    private val bookingsList = mutableListOf<Booking>()
    private lateinit var bookingAdapter: BookingAdapter

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

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if user is signed in
        if (auth.currentUser == null) {
            // User not signed in, redirect to login
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_admin_portal)

        // UI Elements
        bookingRecyclerView = findViewById(R.id.bookingRecyclerView)
        signOutButton = findViewById(R.id.signOutButton)

        // Initialize RecyclerView and Adapter
        bookingAdapter = BookingAdapter(bookingsList) { booking ->
            // Handle item click
            showActionDialog(booking)
        }
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingRecyclerView.adapter = bookingAdapter

        // Fetch booking requests from Firestore
        fetchBookingRequests()

        // Sign-Out Button Logic
        signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun fetchBookingRequests() {
        firestore.collection("Bookings")
            .orderBy("createdAt", Query.Direction.DESCENDING) // Order by createdAt descending
            .get()
            .addOnSuccessListener { result ->
                bookingsList.clear()
                for (document in result) {
                    val booking = Booking(
                        id = document.id,
                        userName = document.getString("userName"),
                        userEmail = document.getString("userEmail"),
                        room = document.getString("room"),
                        date = document.getString("date"),
                        time = document.getString("time"),
                        status = document.getString("status"),
                        createdAt = document.getTimestamp("createdAt") // Retrieve the timestamp
                    )
                    bookingsList.add(booking)
                }
                bookingAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load bookings: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showActionDialog(booking: Booking) {
        val options = arrayOf("Accept & Confirm", "Reject")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an action")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> updateBookingStatus(booking, "confirmed")
                1 -> rejectAndDeleteBooking(booking)
            }
        }
        builder.show()
    }

    private fun updateBookingStatus(booking: Booking, status: String) {
        firestore.collection("Bookings").document(booking.id)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking $status successfully", Toast.LENGTH_LONG).show()
                // Removed sendConfirmationEmail call
                // Update the status locally and refresh the item
                booking.status = status
                bookingAdapter.notifyItemChanged(bookingsList.indexOf(booking))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update booking: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun rejectAndDeleteBooking(booking: Booking) {
        firestore.collection("Bookings").document(booking.id)
            .update("status", "rejected")
            .addOnSuccessListener {
                Toast.makeText(this, "Booking rejected successfully", Toast.LENGTH_LONG).show()
                // Update local list
                val position = bookingsList.indexOf(booking)
                if (position != -1) {
                    booking.status = "rejected"
                    bookingAdapter.notifyItemChanged(position)
                }
                // Delete after a short delay
                Handler(Looper.getMainLooper()).postDelayed({
                    deleteBooking(booking)
                }, 3000) // Delay in milliseconds
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update booking: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteBooking(booking: Booking) {
        firestore.collection("Bookings").document(booking.id)
            .delete()
            .addOnSuccessListener {
                // Remove from list and notify adapter
                val position = bookingsList.indexOf(booking)
                if (position != -1) {
                    bookingsList.removeAt(position)
                    bookingAdapter.notifyItemRemoved(position)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete booking: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun sendConfirmationEmail(booking: Booking, status: String) {
        val userEmail = booking.userEmail

        if (userEmail != null) {
            val subject = "Booking $status"
            val message = "Your booking has been confirmed!"
            sendEmail(userEmail, subject, message)
        }
    }

    private fun sendRejectionEmail(booking: Booking) {
        val userEmail = booking.userEmail

        if (userEmail != null) {
            val subject = "Booking Rejected"
            val message = "We regret to inform you that your booking has been rejected."
            sendEmail(userEmail, subject, message)
        }
    }

    private fun sendEmail(email: String, subject: String, message: String) {
        // Implement email sending logic here
        Toast.makeText(this, "Email sent to $email", Toast.LENGTH_LONG).show()
    }

    // Sign-out method
    private fun signOut() {
        auth.signOut()
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, AdminLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
