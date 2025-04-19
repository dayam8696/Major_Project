package com.example.majorproject.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.majorproject.viewModel.GeminiViewModel
import com.example.majorproject.viewModel.GeminiViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun CustomQueryScreen(viewModelFactory: GeminiViewModelFactory, navController: NavController) {
    Log.d("CustomQueryScreen", "CustomQueryScreen displayed")
    val viewModel: GeminiViewModel = viewModel(factory = viewModelFactory)
    val aiResponses by viewModel.aiResponses.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    var prompt by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var latestPrompt by remember { mutableStateOf<String?>(null) }
    val latestResponse = aiResponses.lastOrNull()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFFE3F2FD))
                )
            )
            .padding(16.dp)
    ) {
        // Header and Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AI Medical Chat",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF0D47A1)
            )
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0D47A1),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Back",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Response Area (Scrollable)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0F4F8))
                .padding(8.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1976D2),
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                latestResponse?.response == "No response" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Failed to fetch response.",
                            color = Color(0xFF424242),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                latestResponse == null && latestPrompt == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ask a medical question below!",
                            color = Color(0xFF424242),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        // User Prompt
                        latestPrompt?.let { promptText ->
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 48.dp, end = 8.dp)
                                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = buildAnnotatedString { append(promptText) },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                        // AI Response
                        latestResponse?.let { response ->
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, end = 48.dp)
                                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = formatCustomMedicalResponse(response.response),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF424242),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = prompt,
                    onValueChange = {
                        prompt = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    placeholder = { Text("Type your question...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF0F4F8),
                        unfocusedContainerColor = Color(0xFFF0F4F8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    isError = errorMessage != null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (prompt.isBlank()) {
                            errorMessage = "Please enter a query."
                            return@Button
                        }
                        Log.d("CustomQueryScreen", "Raw User Prompt: $prompt")
                        latestPrompt = prompt
                        viewModel.fetchAIResponse(prompt)
                        prompt = "" // Clear input after sending
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(1) // Scroll to AI response
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Send",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun formatCustomMedicalResponse(text: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        lines.forEach { line ->
            var styled = false
            val sections = listOf(
                "**Short Description:**" to "Short Description",
                "**Key Symptoms:**" to "Key Symptoms",
                "**Major Risk Factors:**" to "Major Risk Factors",
                "**Essential Recommendations:**" to "Essential Recommendations",
                "**What to Avoid:**" to "What to Avoid"
            )

            for ((mdText, plainText) in sections) {
                if (line.startsWith(mdText)) {
                    pushStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2),
                            fontSize = 16.sp
                        )
                    )
                    append(plainText)
                    pop()
                    append("\n")
                    styled = true
                    break
                }
            }

            if (!styled) {
                val cleanLine = line
                    .replace("*   ", "• ")
                    .replace("**", "")
                    .replace("* ", "• ")
                pushStyle(
                    SpanStyle(
                        color = Color(0xFF424242),
                        fontSize = 14.sp
                    )
                )
                append(cleanLine + "\n")
                pop()
            }
        }
    }
}