package com.example.majorproject.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import java.util.Calendar

@Composable
fun MedicineReminderScreen(context: Context, medicineName: String, hour: Int, minute: Int ,navController: NavController) {
    Button(onClick = {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            requestExactAlarmPermission(context)
        } else {
            scheduleReminder(context, medicineName, hour, minute)
        }
    }) {
        Text("Schedule Reminder")
    }
}

/** ✅ Function to Request Exact Alarm Permission */
fun requestExactAlarmPermission(context: Context) {
    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
        data = Uri.parse("package:" + context.packageName)
    }
    context.startActivity(intent)
}

/** ✅ Function to Schedule the Alarm */
fun scheduleReminder(context: Context, medicineName: String, hour: Int, minute: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("medicine_name", medicineName)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    try {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context, "Reminder set for $hour:$minute", Toast.LENGTH_SHORT).show()
    } catch (e: SecurityException) {
        Log.e("AlarmManager", "Error scheduling reminder: ${e.message}")
        Toast.makeText(context, "Permission required to set exact alarms", Toast.LENGTH_LONG).show()
    }
}

/** ✅ BroadcastReceiver to Handle the Alarm */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("medicine_name") ?: "Medicine"
        Toast.makeText(context, "Time to take your $medicineName!", Toast.LENGTH_LONG).show()
    }
}
