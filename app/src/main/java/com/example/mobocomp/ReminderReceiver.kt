package com.example.mobocomp

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Context
import androidx.room.Room
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val text = intent.getStringExtra("message")
        val uid = intent.getIntExtra("uid", 0)

        MainActivity.showNotification(context,text!!)

        doAsync{
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "reminder").build()
            db.reminderDao().delete(uid)
            db.close()
        }
    }
}