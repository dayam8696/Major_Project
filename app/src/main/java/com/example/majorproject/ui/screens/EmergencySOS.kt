package com.example.majorproject.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.majorproject.db.SharedPreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EmergencySOS(navController: NavController? = null) {
    val context = LocalContext.current
    val sharedPreferenceManager = remember { SharedPreferenceManager(context) }
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

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

    // UI for SOS screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFF6F61), Color(0xFFF8BBD0)),
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            // Text at the top
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Text(
                    text = "In Danger? Press SOS Now",
                    color = Color(0xFF1A237E),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1
                )
            }

            // SOS button in the center
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                var buttonPressed by remember { mutableStateOf(false) }
                val buttonColor by animateColorAsState(
                    targetValue = if (buttonPressed) Color(0xFFD32F2F).copy(alpha = 0.85f) else Color(0xFFD32F2F),
                    animationSpec = tween(200)
                )
                // Pulsing animation for the button
                val pulseScale by animateFloatAsState(
                    targetValue = if (buttonPressed) 1.05f else 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 600, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Button(
                    onClick = {
                        buttonPressed = true
                        val emergencyContact = sharedPreferenceManager.getMobileNumber() ?: "+917518955453"
                        if (emergencyContact.isEmpty()) {
                            Toast.makeText(context, "Emergency contact not set!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Sending SOS to $emergencyContact", Toast.LENGTH_SHORT).show()
                            checkPermissionsAndSendEmergencyMessage(emergencyContact, multiplePermissionsLauncher, fusedLocationClient, context)
                        }
                        buttonPressed = false
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .size(220.dp)
                        .scale(pulseScale)
                        .shadow(12.dp, CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .clip(CircleShape)
                ) {
                    Text(
                        text = "SOS",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }
        }
    }
}

// Check permissions and proceed to send SMS
private fun checkPermissionsAndSendEmergencyMessage(
    emergencyContact: String,
    launcher: androidx.activity.compose.ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
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
        val smsManager = android.telephony.SmsManager.getDefault()
        val locationText = if (location != null) {
            "https://maps.google.com/?q=${location.latitude},${location.longitude}"
        } else {
            "Location not available"
        }
        val message = "Emergency! I require assistance due to a potential medical risk. My location: $locationText"
        smsManager.sendTextMessage(emergencyContact, null, message, null, null)
        Toast.makeText(context, "Emergency SOS sent to $emergencyContact!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to send SOS: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencySOSPreview() {
    EmergencySOS()
}