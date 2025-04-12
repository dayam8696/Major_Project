package com

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.majorproject.ui.screens.PredictionManager
import getKneeHealthInfo

@Composable
fun KneeHealthScreen(navController: NavController) {
    val predictedGrade = PredictionManager.predictionResult
    val kneeInfo = getKneeHealthInfo(predictedGrade)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF95D2B3), Color(0xFF3C8C6C))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back Button with Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Knee Health Report",
                    fontSize = 24.sp,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Information Card
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .padding(8.dp)
                    .animateContentSize()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Predicted Grade: $predictedGrade",
                        fontSize = 22.sp,
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SectionRow(icon = Icons.Default.Info, title = "Description", content = kneeInfo.description)
                    SectionRow(icon = Icons.Default.Info, title = "Symptoms", content = kneeInfo.symptoms)
                    SectionRow(icon = Icons.Default.Info, title = "Risk Factors", content = kneeInfo.riskFactors)
                    SectionRow(icon = Icons.Default.Info, title = "Recommendations", content = kneeInfo.recommendations)
                    SectionRow(icon = Icons.Default.Info, title = "Things to Avoid", content = kneeInfo.avoid)
                }
            }
        }

        // Floating Action Button (Navigate to Exercises/More Info)
        FloatingActionButton(
            onClick = { /* Handle action */ },
            containerColor = Color(0xFF3C8C6C),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .shadow(10.dp, CircleShape)
        ) {
            Icon(Icons.Default.Info, contentDescription = "More Info")
        }
    }
}

// Section Row with Material Icon
@Composable
fun SectionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF3C8C6C),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Text(
            text = content,
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

// Preview Function
@Preview(showBackground = true)
@Composable
fun PreviewKneeHealthScreen() {
    KneeHealthScreen(navController = NavController(LocalContext.current))
}
