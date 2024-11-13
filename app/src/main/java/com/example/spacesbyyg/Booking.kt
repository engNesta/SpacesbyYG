// Booking.kt
package com.example.spacesbyyg

import com.google.firebase.Timestamp

data class Booking(
    val id: String,
    val userName: String?,
    val userEmail: String?,
    val room: String?,
    val date: String?,
    val time: String?,
    var status: String?,
    val createdAt: Timestamp? // Added timestamp field
)
