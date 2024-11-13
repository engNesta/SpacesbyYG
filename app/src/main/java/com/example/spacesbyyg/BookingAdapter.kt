// BookingAdapter.kt
package com.example.spacesbyyg

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(
    private val bookings: List<Booking>,
    private val onItemClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val roomTextView: TextView = itemView.findViewById(R.id.roomTextView)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.dateTimeTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)

        init {
            itemView.setOnClickListener {
                onItemClick(bookings[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.userNameTextView.text = booking.userName ?: "Unknown User"
        holder.roomTextView.text = "Room: ${booking.room ?: "N/A"}"
        holder.dateTimeTextView.text = "Date: ${booking.date ?: "N/A"}, Time: ${booking.time ?: "N/A"}"
        holder.statusTextView.text = "Status: ${booking.status ?: "Pending"}"

        // Change status text color based on status
        when (booking.status) {
            "confirmed" -> holder.statusTextView.setTextColor(Color.GREEN)
            "rejected" -> holder.statusTextView.setTextColor(Color.RED)
            else -> holder.statusTextView.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount(): Int = bookings.size
}
