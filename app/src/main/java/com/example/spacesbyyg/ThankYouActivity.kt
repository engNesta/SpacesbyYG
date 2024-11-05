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
