package com.example.majorproject.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationAlarmScreen(navController: NavController) {
    var medicationName by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val frequencyOptions = listOf("Morning", "Afternoon", "Night", "Twice a day", "Three times a day")
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Medication Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = frequency,
                onValueChange = { },
                label = { Text("Frequency") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                frequencyOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            frequency = option
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (medicationName.isNotBlank() && frequency.isNotBlank()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (!alarmManager.canScheduleExactAlarms()) {
                            // Direct user to this app's alarm permission screen
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                            Toast.makeText(
                                context,
                                "Please allow exact alarms permission for reminders",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // Permission granted
                            scheduleAlarms(medicationName, frequency, context)
                        }
                    } else {
                        // Android version < S, no permission needed
                        scheduleAlarms(medicationName, frequency, context)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Please enter medication name and frequency",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Reminder")
        }
    }
}

private fun scheduleAlarms(medicationName: String, frequency: String, context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val prefs = context.getSharedPreferences("MedicationAlarms", Context.MODE_PRIVATE)
    val editor = prefs.edit()

    val times = when (frequency) {
        "Morning" -> listOf(8 to 0)
        "Afternoon" -> listOf(14 to 0)
        "Night" -> listOf(21 to 0)
        "Twice a day" -> listOf(8 to 0, 18 to 0)
        "Three times a day" -> listOf(8 to 0, 14 to 0, 21 to 0)
        else -> emptyList()
    }

    times.forEachIndexed { index, (hour, minute) ->
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("medication_name", medicationName)
            putExtra("notification_id", index)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            index,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        editor.putLong("alarm_${index}_$medicationName", calendar.timeInMillis)
    }
    editor.apply()

    Toast.makeText(context, "Reminder set for $medicationName", Toast.LENGTH_SHORT).show()
}
