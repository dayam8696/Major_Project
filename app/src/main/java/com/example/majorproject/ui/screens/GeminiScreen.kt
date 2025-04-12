package com.example.majorproject.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.majorproject.viewModel.GeminiViewModel
import com.example.majorproject.viewModel.GeminiViewModelFactory

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GeminiScreen(viewModelFactory: GeminiViewModelFactory) {
    val viewModel: GeminiViewModel = viewModel(factory = viewModelFactory)
    val aiResponses by viewModel.aiResponses.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    val predictionResult = PredictionManager.predictionResult
    var prompt by remember { mutableStateOf(predictionResult ?: "") }

    Log.d("GeminiScreen", "Prediction Result: $predictionResult")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                )
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Medical Guidance",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF0D47A1),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Prediction Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Your Prediction Result",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF0D47A1),
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF424242),
                    modifier = Modifier.padding(start = 32.dp)
                )
            }
        }

        // Action Button
        Button(
            onClick = {
                if (prompt.isNotBlank()) {
                    val aiQuery = generateAIQuery(prompt)
                    Log.d("GeminiScreen", "Generated AI Query: $aiQuery")
                    viewModel.fetchAIResponse(aiQuery)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Text(
                text = "Get Medical Guidance",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading or Content
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF1976D2),
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Analyzing your condition...",
                        color = Color(0xFF424242),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            if (aiResponses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Press the button to get personalized medical guidance based on your prediction result",
                            color = Color(0xFF424242),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(aiResponses) { response ->
                        Log.d("GeminiScreen", "AI Response: ${response.response}")

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = formatMedicalResponse(response.response),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Start,
                                    color = Color(0xFF424242),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun generateAIQuery(prediction: String): String {
    return """
        Based on the knee severity prediction: "$prediction", provide a **concise** summary including:
        - **Short Description** (1-2 lines)
        - **Key Symptoms** (bullet points, max 3)
        - **Major Risk Factors** (bullet points, max 3)
        - **Essential Recommendations** (bullet points, max 3)
        - **What to Avoid** (bullet points, max 3)
        
        Format the response with clear section headers.
        Keep the response brief, professional, and medically accurate.
    """.trimIndent()
}

@Composable
fun formatMedicalResponse(text: String): AnnotatedString {
    return buildAnnotatedString {
        val sections = listOf(
            "**Short Description:**" to "Short Description",
            "**Key Symptoms:**" to "Key Symptoms",
            "**Major Risk Factors:**" to "Major Risk Factors",
            "**Essential Recommendations:**" to "Essential Recommendations",
            "**What to Avoid:**" to "What to Avoid"
        )

        text.split("\n").forEach { line ->
            var styled = false

            for ((mdText, plainText) in sections) {
                if (line.startsWith(mdText)) {
                    pushStyle(SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    ))
                    append(plainText)
                    pop()
                    append("\n")
                    styled = true
                    break
                }
            }

            if (!styled) {
                val cleanLine = line.replace("*   ", "• ")
                    .replace("**", "") // Remove any remaining markdown
                pushStyle(SpanStyle(
                    color = Color(0xFF424242),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ))
                append(cleanLine + "\n")
                pop()
            }
        }
    }
}