package com.example.majorproject.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("medication_name") ?: "Medication"
        val notificationId = intent.getIntExtra("notification_id", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medication_reminder_channel"

        // Create notification channel (required for API 26+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for medication reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app’s icon
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take your $medicationName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Show notification
        notificationManager.notify(notificationId, notification)
    }
}