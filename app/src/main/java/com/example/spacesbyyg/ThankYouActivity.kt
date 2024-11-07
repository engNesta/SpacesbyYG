package com.example.spacesbyyg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.graphics.Color;

class ThankYouActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)

        // Make the status bar transparent
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

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
            // Replace MainActivity with the starting activity of your app
            val intent = Intent(this, MainActivity::class.java)
            // Clear the activity stack to prevent going back to this activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()// Close the app entirely
        }
    }
}
