package com.example.major2o.Fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.major2o.databinding.MediceneReminderBinding
import java.util.Calendar

class MediceneReminder : Fragment() {
    private var _binding: MediceneReminderBinding? = null
    private val binding get() = _binding!!
    private var selectedHour = 0
    private var selectedMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MediceneReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etTime.setOnClickListener {
            showTimePicker()
        }

        binding.btnSetReminder.setOnClickListener {
            setReminder()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute

                // Convert to AM/PM format
                val amPm = if (hourOfDay >= 12) "PM" else "AM"
                val hourIn12Format = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                binding.etTime.setText(String.format("%02d:%02d %s", hourIn12Format, minute, amPm))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 12-hour format
        )
        timePicker.show()
    }

    private fun setReminder() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check for exact alarm permission on Android 12+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }

        val medicineName = binding.etMedicineName.text.toString()

        if (medicineName.isEmpty() || binding.etTime.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter medicine name and time", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(requireContext(), MedicineAlarmReceiver::class.java).apply {
            putExtra("medicine_name", medicineName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)

            // If the selected time is before the current time, schedule it for the next day
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Set exact alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(requireContext(), "Reminder set for $medicineName at ${binding.etTime.text}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
