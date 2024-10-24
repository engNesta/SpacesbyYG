package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ThankYouActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)

        // UI Elements
        val thankYouTextView = findViewById<TextView>(R.id.thankYouTextView)
        val rebookButton = findViewById<Button>(R.id.rebookButton)
        val exitButton = findViewById<Button>(R.id.exitButton)

        // Get the user's name (if passed)
        val name = intent.getStringExtra("name")

        // Set the thank-you message
        thankYouTextView.text = "Thank you, $name!"

        // Logic for Re-book Button: Redirect to RoomSelectionActivity
        rebookButton.setOnClickListener {
            val intent = Intent(this, RoomSelectionActivity::class.java)
            startActivity(intent)
            finish() // Finish current activity so user can't go back to thank-you page
        }

        // Logic for Exit Button: Close the app
        exitButton.setOnClickListener {
            finishAffinity() // Close the app entirely
        }
    }
}
