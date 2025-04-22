package com.example.majorproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CostPrediction(navController: NavController) {
    // State for dropdowns and result
    var selectedDisease by remember { mutableStateOf("Diabetes") }
    var selectedStage by remember { mutableStateOf("1") }
    var selectedHospitalType by remember { mutableStateOf("Government") }
    var result by remember { mutableStateOf("Predicted Cost: ") }
    var isLoading by remember { mutableStateOf(false) }

    // Data for dropdowns
    val diseases = listOf("Diabetes", "Heart Attack", "Knee Replacement")
    val stages = listOf("1", "2", "3", "4")
    val hospitalTypes = listOf("Government", "Private")

    // Hard-coded cost ranges based on dataset
    val costRanges = mapOf(
        "Diabetes" to mapOf(
            "1" to mapOf("Government" to "₹5,000 – ₹10,000", "Private" to "₹15,000 – ₹25,000"),
            "2" to mapOf("Government" to "₹7,500 – ₹15,000", "Private" to "₹20,000 – ₹35,000"),
            "3" to mapOf("Government" to "₹10,000 – ₹20,000", "Private" to "₹30,000 – ₹45,000"),
            "4" to mapOf("Government" to "₹15,000 – ₹30,000", "Private" to "₹40,000 – ₹60,000")
        ),
        "Heart Attack" to mapOf(
            "1" to mapOf("Government" to "₹70,000 – ₹1,00,000", "Private" to "₹1,50,000 – ₹2,50,000"),
            "2" to mapOf("Government" to "₹1,00,000 – ₹1,50,000", "Private" to "₹2,50,000 – ₹4,00,000"),
            "3" to mapOf("Government" to "₹1,50,000 – ₹2,00,000", "Private" to "₹3,50,000 – ₹6,00,000"),
            "4" to mapOf("Government" to "₹2,50,000 – ₹5,00,000", "Private" to "₹5,00,000 – ₹10,00,000")
        ),
        "Knee Replacement" to mapOf(
            "1" to mapOf("Government" to "₹1,50,000 – ₹2,00,000", "Private" to "₹2,50,000 – ₹4,00,000"),
            "2" to mapOf("Government" to "₹1,75,000 – ₹2,25,000", "Private" to "₹3,00,000 – ₹4,50,000"),
            "3" to mapOf("Government" to "₹2,00,000 – ₹3,00,000", "Private" to "₹3,50,000 – ₹5,50,000"),
            "4" to mapOf("Government" to "₹2,50,000 – ₹3,50,000", "Private" to "₹4,50,000 – ₹6,50,000")
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE6F0FA), Color.White)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cost Prediction",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A3C6E),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "For Delhi-based Hospitals",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A3C6E).copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Disease Dropdown
        DropdownMenuField(
            label = "Disease",
            options = diseases,
            selectedOption = selectedDisease,
            onOptionSelected = { selectedDisease = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stage Dropdown
        DropdownMenuField(
            label = "Stage",
            options = stages,
            selectedOption = selectedStage,
            onOptionSelected = { selectedStage = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hospital Type Dropdown
        DropdownMenuField(
            label = "Hospital Type",
            options = hospitalTypes,
            selectedOption = selectedHospitalType,
            onOptionSelected = { selectedHospitalType = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Predict Button
        Button(
            onClick = {
                isLoading = true
                try {
                    // Get predicted cost from hard-coded map
                    val predictedCost = costRanges[selectedDisease]?.get(selectedStage)?.get(selectedHospitalType)
                        ?: "No data available for this combination"
                    println("Predicted Cost: $predictedCost")

                    // Update result
                    result = "Predicted Cost: $predictedCost"
                } catch (e: Exception) {
                    result = "Error: ${e.message}"
                    println("Error during prediction: ${e.message}")
                } finally {
                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A3C6E),
                disabledContainerColor = Color(0xFF1A3C6E).copy(alpha = 0.5f)
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    "Predict Cost",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Result Text
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = result,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A3C6E),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        label,
                        color = Color(0xFF1A3C6E),
                        fontWeight = FontWeight.Medium
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded)
                            androidx.compose.material.icons.Icons.Default.ArrowDropDown
                        else androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color(0xFF1A3C6E)
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF1A3C6E),
                    unfocusedIndicatorColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color(0xFF1A3C6E),
                    unfocusedTextColor = Color(0xFF1A3C6E)
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                color = Color(0xFF1A3C6E),
                                fontWeight = FontWeight.Medium
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier.background(
                            if (option == selectedOption)
                                Color(0xFFE6F0FA)
                            else Color.White
                        )
                    )
                }
            }
        }
    }
}