package com.example.major2o.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast
import com.example.major2o.R

class MedicineAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val medicineName = intent?.getStringExtra("medicine_name") ?: "Medicine"

        Toast.makeText(context, "Time to take your $medicineName!", Toast.LENGTH_LONG).show()

        // Play alarm sound
        val mediaPlayer = MediaPlayer.create(context, R.raw.name_ringtone)
        mediaPlayer?.start()
    }
}
