package com.example.majorproject.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.majorproject.MainActivity
import com.example.majorproject.R

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val medicineName = intent?.getStringExtra("medicine_name") ?: "Unknown"
        Log.d("ReminderBroadcastReceiver", "Alarm received for medicine: $medicineName")

        // Show notification
        if (context != null) {
            showNotification(context, medicineName)
        }
    }

    private fun showNotification(context: Context, medicineName: String) {
        val channelId = "MedicineReminderChannel"

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Medicine Reminder")
            .setContentText("Time to take your medicine: $medicineName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Medicine Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notification)
    }
}
