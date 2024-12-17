package com.example.spacesbyyg

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsActivity : AppCompatActivity() {
    // UI and Firestore references
    private lateinit var barChart: BarChart
    private lateinit var firestore: FirebaseFirestore
    private lateinit var monthSpinner: Spinner
    private lateinit var yearEditText: TextInputEditText
    private lateinit var fetchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to our analytics layout
        setContentView(R.layout.activity_analytics)

        // Set up the toolbar from the layout for a modern app bar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.analyticsToolbar)
        setSupportActionBar(toolbar)

        // Initialize UI elements
        barChart = findViewById(R.id.barChart)
        monthSpinner = findViewById(R.id.monthSpinner)
        yearEditText = findViewById(R.id.yearEditText)
        fetchButton = findViewById(R.id.fetchButton)

        // Initialize Firestore for database queries
        firestore = FirebaseFirestore.getInstance()

        // Set up the month spinner with month names
        setupMonthSpinner()

        // Apply styling and configuration to the chart
        setupChartAppearance()

        // Get current month and year to pre-fill the UI
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) // Note: months are 0-based
        val currentYear = calendar.get(Calendar.YEAR)

        // Set the spinner to current month and year field to current year
        monthSpinner.setSelection(currentMonth)
        yearEditText.setText(currentYear.toString())

        // Fetch and display data for the current month/year on activity start
        fetchMonthlyBookingsData(currentMonth, currentYear)

        // When the fetch button is clicked, get the selected month/year and fetch data again
        fetchButton.setOnClickListener {
            val selectedMonthIndex = monthSpinner.selectedItemPosition
            val selectedYearText = yearEditText.text?.toString()
            val selectedYear = selectedYearText?.toIntOrNull()

            // Validate the year and fetch data if valid
            if (selectedYear != null) {
                fetchMonthlyBookingsData(selectedMonthIndex, selectedYear)
            } else {
                Toast.makeText(this, "Please enter a valid year.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMonthSpinner() {
        // List of months to display in the spinner
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        // Create an adapter to populate the spinner with month names
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)
        monthSpinner.adapter = adapter
    }

    private fun setupChartAppearance() {
        // Remove any grid background from the chart for a cleaner look
        barChart.setDrawGridBackground(false)

        // Disable the default description and legend for now (we set our own description later)
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false

        // Configure the X-axis at the bottom, no grid lines, and black text for readability
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textColor = Color.BLACK

        // Configure the left Y-axis, no grid lines, black text
        val yAxisLeft = barChart.axisLeft
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.textColor = Color.BLACK

        // Force Y-axis to show whole numbers by setting granularity and custom formatter
        yAxisLeft.granularity = 1f
        yAxisLeft.setGranularityEnabled(true)
        yAxisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Convert the float value to an integer string (removes decimals)
                return value.toInt().toString()
            }
        }

        // Disable the right Y-axis as it's not needed
        val yAxisRight = barChart.axisRight
        yAxisRight.isEnabled = false

        // Add a vertical animation for a smoother appearance and set a custom "no data" message
        barChart.animateY(1000)
        barChart.setNoDataTextColor(Color.WHITE)
        barChart.setNoDataText("No data available for the selected month.")
    }

    private fun fetchMonthlyBookingsData(month: Int, year: Int) {
        // We'll parse the "date" field which looks like "Tue, Dec 17, 2024"
        val sdf = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())

        // Fetch all bookings (not filtering by createdAt)
        firestore.collection("Bookings")
            .get()
            .addOnSuccessListener { documents ->
                val roomCounts = mutableMapOf<String, Int>()

                for (doc in documents) {
                    val status = doc.getString("status")
                    val dateStr = doc.getString("date")

                    // Only count confirmed bookings with a valid date
                    if (status == "confirmed" && dateStr != null) {
                        val bookingDate = sdf.parse(dateStr)
                        if (bookingDate != null) {
                            val c = Calendar.getInstance()
                            c.time = bookingDate
                            val bookingMonth = c.get(Calendar.MONTH)
                            val bookingYear = c.get(Calendar.YEAR)

                            // Check if the booking's month and year match the selected month and year
                            if (bookingMonth == month && bookingYear == year) {
                                val room = doc.getString("room")
                                if (room != null) {
                                    roomCounts[room] = (roomCounts[room] ?: 0) + 1
                                }
                            }
                        }
                    }
                }

                // If no confirmed bookings found for that month/year, clear chart and inform the user
                if (roomCounts.isEmpty()) {
                    barChart.clear()
                    Toast.makeText(this, "No accepted bookings found for this month.", Toast.LENGTH_SHORT).show()
                } else {
                    // If we have data, display it on the chart
                    displayBarChart(roomCounts, month, year)
                }
            }
            .addOnFailureListener {
                // If the query fails, show an error message
                Toast.makeText(this, "Failed to fetch data: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun displayBarChart(roomCounts: Map<String, Int>, month: Int, year: Int) {
        // Define all the rooms you want to show, ensuring consistent labeling
        val allRooms = listOf("Conference Room", "Creative Room")

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        var index = 0f
        // Iterate through allRooms to ensure both appear even if count is zero
        for (roomName in allRooms) {
            val count = roomCounts[roomName] ?: 0
            entries.add(BarEntry(index, count.toFloat()))
            labels.add(roomName)
            index++
        }

        // Create a BarDataSet to hold the data for the chart
        val dataSet = BarDataSet(entries, "")
        dataSet.color = ContextCompat.getColor(this, R.color.lime_green) // Give bars a green color
        dataSet.valueTextColor = Color.WHITE // Values above bars in white for contrast
        dataSet.valueTextSize = 14f // Larger text size for readability
        dataSet.setDrawValues(true) // Ensure values are shown above bars

        val barData = BarData(dataSet)
        barData.barWidth = 0.8f
        barData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Show values as integers (no decimals)
                return value.toInt().toString()
            }
        })

        // Set the data for the chart
        barChart.data = barData
        // Fit the bars so they nicely align with the chart edges
        barChart.setFitBars(true)

        // Get the month name from the spinner for chart description
        val monthName = (monthSpinner.adapter.getItem(month) as String)
        barChart.description.isEnabled = true
        barChart.description.text = "Confirmed Bookings for $monthName $year"
        barChart.description.textColor = Color.WHITE

        // Set the labels (room names) on the X-axis
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.labelCount = labels.size
        barChart.xAxis.textSize = 12f

        // Refresh the chart to display new data
        barChart.invalidate()
    }
}
