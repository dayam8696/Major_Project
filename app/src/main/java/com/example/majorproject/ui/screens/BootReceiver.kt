package com.example.majorproject.ui.screens


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // Restore alarms from SharedPreferences
            val prefs = context.getSharedPreferences("MedicationAlarms", Context.MODE_PRIVATE)
            val alarms = prefs.all

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarms.forEach { (key, value) ->
                val parts = key.split("_")
                if (parts.size == 3 && parts[0] == "alarm") {
                    val index = parts[1].toInt()
                    val medicationName = parts[2]
                    val timeMillis = value as Long

                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = timeMillis
                        if (timeInMillis <= System.currentTimeMillis()) {
                            add(Calendar.DAY_OF_MONTH, 1)
                        }
                    }

                    val alarmIntent = Intent(context, ReminderReceiver::class.java).apply {
                        putExtra("medication_name", medicationName)
                        putExtra("notification_id", index)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        index,
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }
        }
    }
}