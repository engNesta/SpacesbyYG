package com.example.spacesbyyg

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val TAG = "FirestoreTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Adjust window insets for better display (optional, based on your original setup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        // Fetch the specific document from "Bookings" collection by ID
        val documentId = "RqegQ1ye5GnP6RZfEdFM"  // Your specific document ID
        val resultTextView = findViewById<TextView>(R.id.resultTextView)  // TextView to display result

        db.collection("Bookings").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data
                    Log.d(TAG, "Document data: $data")

                    // Display the fetched data in the TextView
                    resultTextView.text = "Document Data: $data"
                } else {
                    Log.d(TAG, "No such document!")
                    resultTextView.text = "No such document!"
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error fetching document", e)
                resultTextView.text = "Error fetching document: ${e.message}"
            }
    }
}
