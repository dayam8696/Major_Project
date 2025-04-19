package com.example.majorproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Add DiabetesResultHolder here
object ResultHolder {
    var result: Float? = null
}

@Composable
fun DiabetesResultScreen(navController: NavController) {
    val result = DiabetesResultHolder.result ?: 0f

    val percentage = "%.2f".format(result * 100)

    val (status, statusColor, emoji) = when {
        result > 0.75f -> Triple("High Risk", Color(0xFFD32F2F), "⚠️")
        result > 0.5f -> Triple("Moderate Risk", Color(0xFFFFA000), "⚠️")
        result > 0.25f -> Triple("Low to Moderate Risk", Color(0xFF388E3C), "✅")
        else -> Triple("Low Risk", Color(0xFF4CAF50), "✅")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Your Diabetes Risk",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = statusColor
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$emoji $status",
                        color = statusColor,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Text(
                    text = when {
                        result > 0.75f -> "⚠️ You are at high risk of Diabetes. Consult a doctor immediately."
                        result > 0.5f -> "⚠️ Moderate risk detected. A medical check-up is advisable."
                        result > 0.25f -> "✅ Your risk is manageable. Maintain a healthy diet and lifestyle."
                        else -> "✅ Very low risk. Keep maintaining your healthy habits!"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back")
                    }
                    Button(
                        onClick = {
                            // Navigate to GeminiScreen; result is already in DiabetesResultHolder
                            navController.navigate("DiabetesGeminiScreen")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF03DAC5),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Learn More")
                    }
                }
            }
        }
    }
}