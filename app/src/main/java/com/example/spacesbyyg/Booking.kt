// Booking.kt
package com.example.spacesbyyg

data class Booking(
    val id: String,
    val userName: String?,
    val userEmail: String?,
    val room: String?,
    val day: String?,
    val time: String?,
    var status: String?
)
