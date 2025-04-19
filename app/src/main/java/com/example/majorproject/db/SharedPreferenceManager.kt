package com.example.majorproject.db


import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Function to save mobile number
    fun saveMobileNumber(mobileNumber: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber)
        editor.apply()
    }

    // Function to retrieve mobile number
    fun getMobileNumber(): String? {
        return sharedPreferences.getString(KEY_MOBILE_NUMBER, "")
    }

    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val KEY_MOBILE_NUMBER = "mobile_number"
    }
}
