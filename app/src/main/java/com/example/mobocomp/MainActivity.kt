package com.example.mobocomp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import android.widget.Button
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

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


    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java,
                "reminder").build()
            val reminders = db.reminderDao().getReminders()
            db.close()

            uiThread {

                if (reminders.isNotEmpty()) {
                    val adapter = ReminderAdapter(applicationContext, reminders)
                    list.adapter = adapter
                } else {
                    toast("No reminders yet")
                }
            }
        }
    }

}
