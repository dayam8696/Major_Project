@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.majorproject.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.majorproject.db.SharedPreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Composable
fun EmergencyContactScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferenceManager = remember { SharedPreferenceManager(context) }
    var mobileNumber by remember { mutableStateOf(TextFieldValue("")) }
    var savedContact by remember { mutableStateOf(sharedPreferenceManager.getMobileNumber() ?: "") }
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // Launcher for picking contact
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { contactUri ->
            try {
                // Step 1: Get the contact ID from the contact URI
                val contactId = context.contentResolver.query(contactUri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    } else {
                        null
                    }
                }

                if (contactId != null) {
                    // Step 2: Query the phone number using the contact ID
                    val phoneCursor = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(contactId),
                        null
                    )

                    phoneCursor?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            if (phoneIndex >= 0) { // Ensure the column exists
                                val rawNumber = cursor.getString(phoneIndex) ?: ""
                                // Clean the phone number (keep only digits and leading +)
                                val cleanedNumber = rawNumber.replace("[^0-9+]".toRegex(), "")
                                // Optional: Format the number for display (e.g., for US numbers)
                                val formattedNumber = if (cleanedNumber.startsWith("+1") && cleanedNumber.length == 12) {
                                    // Format US number: +1XXXXXXXXXX -> (XXX) XXX-XXXX
                                    val digits = cleanedNumber.substring(2) // Skip +1
                                    "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
                                } else {
                                    cleanedNumber // Use cleaned number as fallback
                                }
                                if (cleanedNumber.isNotEmpty()) {
                                    mobileNumber = TextFieldValue(formattedNumber)
                                    Toast.makeText(context, "Contact selected: $formattedNumber", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "No phone number found for this contact", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "No phone number found for this contact", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "Failed to query phone number", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to retrieve contact ID", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error retrieving contact: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace() // Log the error for debugging
            }
        } ?: run {
            Toast.makeText(context, "No contact selected", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher for requesting contact permission
    val contactPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            contactPickerLauncher.launch()
        } else {
            Toast.makeText(context, "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher for requesting SMS and location permissions
    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.SEND_SMS] == true
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (smsGranted && locationGranted) {
            val emergencyContact = sharedPreferenceManager.getMobileNumber() ?: "+917518955453"
            getLastLocationAndSendSMS(emergencyContact, fusedLocationClient, context)
        } else {
            Toast.makeText(context, "SMS or location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Gradient background for the screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Text(
                text = "Emergency Contact",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 36.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Card for main content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Saved contact display
                    Text(
                        text = "Saved Contact: ${savedContact.ifEmpty { "None" }}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        ),
                        textAlign = TextAlign.Center
                    )

                    // Mobile number input field
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("Mobile Number", style = MaterialTheme.typography.bodyLarge) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.primary
//                            containerColor = MaterialTheme.colorScheme.surface,
//                            focusedBorderColor = MaterialTheme.colorScheme.primary,
//                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
//                            focusedLabelColor = MaterialTheme.colorScheme.primary,
//                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )



                    // Save button
                    var saveButtonPressed by remember { mutableStateOf(false) }
                    val saveButtonColor by animateColorAsState(
                        targetValue = if (saveButtonPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary,
                        animationSpec = tween(200)
                    )
                    Button(
                        onClick = {
                            saveButtonPressed = true
                            if (mobileNumber.text.isNotEmpty()) {
                                sharedPreferenceManager.saveMobileNumber(mobileNumber.text)
                                savedContact = mobileNumber.text
                                Toast.makeText(context, "Mobile number saved!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Please enter a mobile number", Toast.LENGTH_LONG).show()
                            }
                            saveButtonPressed = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(saveButtonColor, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                                )
                            ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text(
                            text = "Save Contact",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Select contact button
                    var selectButtonPressed by remember { mutableStateOf(false) }
                    val selectButtonColor by animateColorAsState(
                        targetValue = if (selectButtonPressed) MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.secondary,
                        animationSpec = tween(200)
                    )
                    Button(
                        onClick = {
                            selectButtonPressed = true
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                                contactPickerLauncher.launch()
                            } else {
                                contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                            selectButtonPressed = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(selectButtonColor, MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f))
                                )
                            ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text(
                            text = "Select Contact",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    // Emergency button
                    var emergencyButtonPressed by remember { mutableStateOf(false) }
                    val emergencyButtonColor by animateColorAsState(
                        targetValue = if (emergencyButtonPressed) Color(0xFFD32F2F).copy(alpha = 0.85f) else Color(0xFFD32F2F),
                        animationSpec = tween(200)
                    )
                    Button(
                        onClick = {
                            emergencyButtonPressed = true
                            val emergencyContact = sharedPreferenceManager.getMobileNumber() ?: "+917518955453"
                            if (emergencyContact.isEmpty()) {
                                Toast.makeText(context, "Emergency contact not set!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Sending SMS to $emergencyContact", Toast.LENGTH_SHORT).show()
                                checkPermissionsAndSendEmergencyMessage(emergencyContact, multiplePermissionsLauncher, fusedLocationClient, context)
                            }
                            emergencyButtonPressed = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(emergencyButtonColor)
                            .border(2.dp, Color(0xFFB71C1C), RoundedCornerShape(16.dp)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                    ) {
                        Text(
                            text = "Emergency",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Extension to launch contact picker
fun ManagedActivityResultLauncher<Intent, Uri?>.launch() {
    launch(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI))
}

// Check permissions and proceed to send SMS
private fun checkPermissionsAndSendEmergencyMessage(
    emergencyContact: String,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    fusedLocationClient: FusedLocationProviderClient,
    context: android.content.Context
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) {
        launcher.launch(
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION)
        )
    } else {
        getLastLocationAndSendSMS(emergencyContact, fusedLocationClient, context)
    }
}

// Get last known location and send SMS
private fun getLastLocationAndSendSMS(
    emergencyContact: String,
    fusedLocationClient: FusedLocationProviderClient,
    context: android.content.Context
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
        return
    }

    fusedLocationClient.lastLocation.addOnCompleteListener { task ->
        if (task.isSuccessful && task.result != null) {
            val location = task.result
            sendSMSWithLocation(emergencyContact, location, context)
        } else {
            Toast.makeText(context, "Could not retrieve location", Toast.LENGTH_SHORT).show()
        }
    }
}

// Send SMS with location
private fun sendSMSWithLocation(
    emergencyContact: String,
    location: android.location.Location?,
    context: android.content.Context
) {
    try {
        val smsManager = SmsManager.getDefault()
        val locationText = if (location != null) {
            "https://maps.google.com/?q=${location.latitude},${location.longitude}"
        } else {
            "Location not available"
        }
        val message = "Emergency! I require assistance due to a potential medical risk. My location: $locationText"
        smsManager.sendTextMessage(emergencyContact, null, message, null, null)
        Toast.makeText(context, "Emergency SMS sent to $emergencyContact!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
    }
}