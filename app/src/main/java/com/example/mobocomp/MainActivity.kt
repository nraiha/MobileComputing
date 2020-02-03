package com.example.mobocomp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button_click = findViewById(R.id.button_click) as Button
        // set on-click listener
        button_click.setOnClickListener {
            // your code to perform when the user clicks on the button
            Toast.makeText(this@MainActivity,"Click clack.", Toast.LENGTH_SHORT).show()
        }

        var fabOpened = false

        fab1.setOnClickListener {
            if (!(fabOpened)) {
                fabOpened = true
                fab_map.animate().translationY(-resources.getDimension(R.dimen.standard_66))
                fab_time.animate().translationY(-resources.getDimension(R.dimen.standard_166))
            } else {
                fabOpened = false
                fab_map.animate().translationY(0f)
                fab_time.animate().translationY(0f)
            }
        }

        fab_time.setOnClickListener {
            val intent = Intent(applicationContext, TimeActivity::class.java)
            startActivity(intent)
        }

        fab_map.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
        }

        val data = arrayOf("Oulu", "Helsinki", "Tampere")
        val reminderAdapter = ReminderAdapter(applicationContext, data)
        list.adapter = reminderAdapter


    }
}
