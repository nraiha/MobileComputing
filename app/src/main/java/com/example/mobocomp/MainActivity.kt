package com.example.mobocomp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get reference to button
        val button_click = findViewById(R.id.button_click) as Button
        // set on-click listener
        button_click.setOnClickListener {
            // your code to perform when the user clicks on the button
            Toast.makeText(this@MainActivity, "Click clack.", Toast.LENGTH_SHORT).show()
        }
    }
}
