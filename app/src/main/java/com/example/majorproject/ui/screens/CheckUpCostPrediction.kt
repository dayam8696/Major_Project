package com.example.majorproject.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MedicalCheckupScreen(navController: NavController) {
    // State for checkbox selections
    var isDiabetesChecked by remember { mutableStateOf(false) }
    var isHeartChecked by remember { mutableStateOf(false) }
    var isKneeChecked by remember { mutableStateOf(false) }

    // State for hospital type selection
    var selectedHospitalType by remember { mutableStateOf("Private") }
    val hospitalTypes = listOf("Private", "Government")
    var expanded by remember { mutableStateOf(false) }

    // State for total cost and test details
    var totalCost by remember { mutableStateOf(0) }
    var testDetails by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }

    // Scroll state for the column
    val scrollState = rememberScrollState()

    // Color palette
    val primaryColor = Color(0xFF0288D1) // Teal-blue for healthcare theme
    val accentColor = Color(0xFF4CAF50) // Green for buttons and highlights
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFE3F2FD), Color.White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Medical Check-Up Calculator",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = primaryColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Note about Delhi-based hospitals
        Text(
            text = "Note: Costs are specific to Delhi-based hospitals",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Hospital Type Selection Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Hospital Type",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(
                        value = selectedHospitalType,
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, primaryColor, RoundedCornerShape(8.dp))
                            .clickable { expanded = true },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Toggle dropdown",
                                    tint = primaryColor
                                )
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        hospitalTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, color = primaryColor) },
                                onClick = {
                                    selectedHospitalType = type
                                    expanded = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Check-Ups Selection Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Check-Ups",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Diabetes Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDiabetesChecked,
                        onCheckedChange = { isDiabetesChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = accentColor,
                            uncheckedColor = Color.Gray
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Diabetes Check-Up",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.Black
                    )
                }

                // Heart Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isHeartChecked,
                        onCheckedChange = { isHeartChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = accentColor,
                            uncheckedColor = Color.Gray
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Heart Check-Up",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.Black
                    )
                }

                // Knee Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isKneeChecked,
                        onCheckedChange = { isKneeChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = accentColor,
                            uncheckedColor = Color.Gray
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Knee Severity Check-Up",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.Black
                    )
                }
            }
        }

        // Check Cost Button
        Button(
            onClick = {
                totalCost = 0
                val details = StringBuilder()
                showResults = true

                if (isDiabetesChecked) {
                    val cost = if (selectedHospitalType == "Private") 1499 else 500
                    totalCost += cost
                    details.append("Diabetes Check-Up (₹$cost):\n")
                    details.append("- Fasting Blood Sugar (FBS)\n")
                    details.append("- HbA1c\n")
                    details.append("- Lipid Profile\n")
                    details.append("- Urine Tests\n\n")
                }

                if (isHeartChecked) {
                    val cost = if (selectedHospitalType == "Private") 1999 else 800
                    totalCost += cost
                    details.append("Heart Check-Up (₹$cost):\n")
                    details.append("- Complete Blood Count (CBC)\n")
                    details.append("- Lipid Profile\n")
                    details.append("- Electrocardiogram (ECG)\n")
                    details.append("- Treadmill Test (TMT)\n")
                    details.append("- Cardiac Markers\n\n")
                }

                if (isKneeChecked) {
                    val cost = if (selectedHospitalType == "Private") 4999 else 2000
                    totalCost += cost
                    details.append("Knee Severity Check-Up (₹$cost):\n")
                    details.append("- X-ray (Knee)\n")
                    details.append("- MRI Knee Scan\n")
                    details.append("- Orthopedic Consultation\n")
                    details.append("- Blood Tests (ESR, CRP)\n\n")
                }

                testDetails = if (details.isEmpty()) {
                    "No check-ups selected."
                } else {
                    details.toString().trim()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(accentColor, Color(0xFF81C784))
                    )
                )
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        ) {
            Text(
                text = "Check Cost",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Results Card
        AnimatedVisibility(visible = showResults) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Total Cost: ₹$totalCost",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tests Included:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = testDetails,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}