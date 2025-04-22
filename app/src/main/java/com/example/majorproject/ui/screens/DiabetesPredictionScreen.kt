package com.example.majorproject.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.majorproject.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
fun DiabetesPredictionScreen(navController: NavController) {
    val context = LocalContext.current

    // State for all input fields
    var gender by remember { mutableStateOf("Male") }
    var age by remember { mutableStateOf("") }
    var hypertension by remember { mutableStateOf("") }
    var heartDisease by remember { mutableStateOf("") }
    var smokingHistory by remember { mutableStateOf("Never") }
    var bmi by remember { mutableStateOf("") }
    var hba1cLevel by remember { mutableStateOf("") }
    var bloodGlucoseLevel by remember { mutableStateOf("") }

    // State for BMI calculator dialog
    var showBmiDialog by remember { mutableStateOf(false) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F7FA),
                        Color(0xFFE6ECF0)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Diabetes Risk Prediction",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A3C6D),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Enter your details to assess your risk",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Input Fields Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Gender Selection
                    InputSection(label = "Gender") {
                        Spinner(
                            items = listOf("Male", "Female"),
                            selectedItem = gender,
                            onItemSelected = { gender = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Age Input
                    InputSection(label = "Age") {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text("Enter your age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Hypertension Input
                    InputSection(label = "Hypertension (Yes=1, No=0)") {
                        OutlinedTextField(
                            value = hypertension,
                            onValueChange = { hypertension = it },
                            label = { Text("Enter 1 or 0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Heart Disease Input
                    InputSection(label = "Heart Disease (Yes=1, No=0)") {
                        OutlinedTextField(
                            value = heartDisease,
                            onValueChange = { heartDisease = it },
                            label = { Text("Enter 1 or 0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Smoking History
                    InputSection(label = "Smoking History") {
                        Spinner(
                            items = listOf("Never", "Current", "Former"),
                            selectedItem = smokingHistory,
                            onItemSelected = { smokingHistory = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // BMI Input with Calculator Button
                    InputSection(label = "BMI") {
                        Column {
                            OutlinedTextField(
                                value = bmi,
                                onValueChange = { bmi = it },
                                label = { Text("Enter your BMI") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color(0xFF1976D2),
                                    unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            )
                            TextButton(
                                onClick = { showBmiDialog = true },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "Calculate BMI",
                                    color = Color(0xFF1976D2),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // HbA1c Level
                    InputSection(label = "HbA1c Level") {
                        OutlinedTextField(
                            value = hba1cLevel,
                            onValueChange = { hba1cLevel = it },
                            label = { Text("Enter HbA1c Level") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Blood Glucose Level
                    InputSection(label = "Blood Glucose Level") {
                        OutlinedTextField(
                            value = bloodGlucoseLevel,
                            onValueChange = { bloodGlucoseLevel = it },
                            label = { Text("Enter Glucose Level") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            // BMI Calculator Dialog
            if (showBmiDialog) {
                Dialog(onDismissRequest = { showBmiDialog = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Calculate BMI",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A3C6D)
                            )

                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = { Text("Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = height,
                                onValueChange = { height = it },
                                label = { Text("Height (m)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { showBmiDialog = false }
                                ) {
                                    Text("Cancel", color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (weight.isNotEmpty() && height.isNotEmpty()) {
                                            try {
                                                val w = weight.toFloat()
                                                val h = height.toFloat()
                                                if (h > 0) {
                                                    val calculatedBmi = w / (h * h)
                                                    bmi = String.format("%.1f", calculatedBmi)
                                                    showBmiDialog = false
                                                    weight = ""
                                                    height = ""
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Height must be greater than 0",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } catch (e: NumberFormatException) {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter valid numbers",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Please enter both weight and height",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1976D2)
                                    )
                                ) {
                                    Text("Calculate", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Predict Button
            Button(
                onClick = {
                    if (validateInputs(
                            age, hypertension, heartDisease,
                            bmi, hba1cLevel, bloodGlucoseLevel
                        )) {
                        val prediction = predictDiabetesRisk(
                            context = context,
                            gender = gender,
                            age = age.toFloat(),
                            hypertension = hypertension.toFloat(),
                            heartDisease = heartDisease.toFloat(),
                            smokingHistory = smokingHistory,
                            bmi = bmi.toFloat(),
                            hba1cLevel = hba1cLevel.toFloat(),
                            bloodGlucoseLevel = bloodGlucoseLevel.toFloat()
                        )
                        Toast.makeText(context, "Prediction: $prediction", Toast.LENGTH_SHORT).show()
                        DiabetesResultHolder.result = prediction
                        navController.navigate(route = "DiabetesResultScreen")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1976D2),
                                    Color(0xFF42A5F5)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Predict Risk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun InputSection(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A3C6D)
        )
        content()
    }
}

@Composable
fun Spinner(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF1A3C6D)
            ),
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
        ) {
            Text(
                text = selectedItem,
                fontSize = 16.sp
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item, fontSize = 16.sp) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

fun validateInputs(
    age: String,
    hypertension: String,
    heartDisease: String,
    bmi: String,
    hba1cLevel: String,
    bloodGlucoseLevel: String
): Boolean {
    return age.isNotEmpty() && hypertension.isNotEmpty() &&
            heartDisease.isNotEmpty() && bmi.isNotEmpty() &&
            hba1cLevel.isNotEmpty() && bloodGlucoseLevel.isNotEmpty()
}

fun predictDiabetesRisk(
    context: Context,
    gender: String,
    age: Float,
    hypertension: Float,
    heartDisease: Float,
    smokingHistory: String,
    bmi: Float,
    hba1cLevel: Float,
    bloodGlucoseLevel: Float
): Float {
    val genderOneHot = when (gender) {
        "Male" -> floatArrayOf(1.0f, 0.0f)
        "Female" -> floatArrayOf(0.0f, 1.0f)
        else -> floatArrayOf(0.0f, 0.0f)
    }

    val smokingHistoryOneHot = when (smokingHistory) {
        "Never" -> floatArrayOf(1.0f, 0.0f, 0.0f)
        "Current" -> floatArrayOf(0.0f, 1.0f, 0.0f)
        "Former" -> floatArrayOf(0.0f, 0.0f, 1.0f)
        else -> floatArrayOf(0.0f, 0.0f, 0.0f)
    }

    val byteBuffer = ByteBuffer.allocateDirect(8 * 4).apply {
        order(ByteOrder.nativeOrder())
        putFloat(genderOneHot[0])
        putFloat(genderOneHot[1])
        putFloat(smokingHistoryOneHot[0])
        putFloat(smokingHistoryOneHot[1])
        putFloat(smokingHistoryOneHot[2])
        putFloat(age)
        putFloat(bmi)
        putFloat(bloodGlucoseLevel)
    }

    val model = Model.newInstance(context)
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 8), DataType.FLOAT32)
    inputFeature0.loadBuffer(byteBuffer)

    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer
    val prediction = outputFeature0.floatArray[0]

    model.close()

    return prediction
}

object DiabetesResultHolder {
    var result: Float? = null
}