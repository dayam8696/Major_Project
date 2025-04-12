package com.example.majorproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.majorproject.R

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Light background
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Home",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A73E8) // Blue accent
            )
            IconButton(
                onClick = { /* Settings action */ },
                modifier = Modifier
                    .background(Color(0xFFE3F2FD), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_settings_24),
                    contentDescription = "Settings",
                    tint = Color(0xFF1A73E8)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Favorite Tools Section
        Text(
            text = "Favorite Tools",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50) // Darker shade
        )
        Spacer(modifier = Modifier.height(12.dp))

        FavoriteToolItem(
            title = "Joint Analysis",
            description = "Analyze joint condition",
            imageRes = R.drawable.joint,
            gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF81C784)), // Green gradient
            navController = navController
        )
        Spacer(modifier = Modifier.height(12.dp))
        FavoriteToolItem(
            title = "Graph Analysis",
            description = "Analyze your medical data",
            imageRes = R.drawable.graph,
            gradientColors = listOf(Color(0xFFFF9800), Color(0xFFFFB300)), // Orange gradient
            navController = navController
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Health Data Section
        Text(
            text = "Health Data",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50)
        )
        Spacer(modifier = Modifier.height(12.dp))

        HealthDataItem(title = "Predict Disease", iconTint = Color(0xFFE91E63), navController = navController)
        Spacer(modifier = Modifier.height(8.dp))
        HealthDataItem(title = "Nearby Hospitals", iconTint = Color(0xFF2196F3), navController = navController)
        Spacer(modifier = Modifier.height(8.dp))
        HealthDataItem(title = "Medicine Reminder", iconTint = Color(0xFF9C27B0), navController = navController) // Updated
        Spacer(modifier = Modifier.height(8.dp))
        HealthDataItem(title = "Inventory Management", iconTint = Color(0xFF009688), navController = navController)

    }
}

@Composable
fun FavoriteToolItem(
    title: String,
    description: String,
    imageRes: Int,
    gradientColors: List<Color>,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (title == "Joint Analysis") {
                            navController.navigate("kneePredictionScreen")
                        }
                        if (title == "Graph Analysis") {
                            navController.navigate("changeScreen")
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = gradientColors[0]
                    )
                ) {
                    Text(
                        text = "Start",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
fun HealthDataItem(title: String, iconTint: Color , navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable {
            if (title == "Medicine Reminder") {
                navController.navigate("MedicineReminderScreen")
            }
        },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {  }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                contentDescription = "Arrow Right",
                tint = iconTint
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    // NavController cannot be instantiated in preview, so we pass a dummy function
//    HomeScreen(navController = object : NavController(LocalContext.current) {})
//}