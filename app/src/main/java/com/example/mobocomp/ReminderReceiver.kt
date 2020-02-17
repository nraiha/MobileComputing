package com.example.mobocomp

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Context
import org.jetbrains.anko.toast

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val text = intent.getStringExtra("message")
        context.toast(text!!)
    }
}