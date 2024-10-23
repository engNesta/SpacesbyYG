package com.example.spacesbyyg

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ThankYouActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)

        // UI Elements
        val thankYouTextView = findViewById<TextView>(R.id.thankYouTextView)

        // Get the user's name (if passed)
        val name = intent.getStringExtra("name")

        // Set the thank-you message
        thankYouTextView.text = "Thank you, $name!"
    }
}
