package com.example.majorproject.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Diabetes Risk Prediction",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Gender Selection
        Text(
            text = "Gender",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spinner(
            items = listOf("Male", "Female"),
            selectedItem = gender,
            onItemSelected = { gender = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Age Input
        Text(
            text = "Age",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Enter your age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Hypertension Input
        Text(
            text = "Hypertension (Yes=1, No=0)",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = hypertension,
            onValueChange = { hypertension = it },
            label = { Text("Enter 1 or 0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Heart Disease Input
        Text(
            text = "Heart Disease (Yes=1, No=0)",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = heartDisease,
            onValueChange = { heartDisease = it },
            label = { Text("Enter 1 or 0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Smoking History
        Text(
            text = "Smoking History",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spinner(
            items = listOf("Never", "Current", "Former"),
            selectedItem = smokingHistory,
            onItemSelected = { smokingHistory = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // BMI Input
        Text(
            text = "BMI",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = bmi,
            onValueChange = { bmi = it },
            label = { Text("Enter your BMI") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // HbA1c Level
        Text(
            text = "HbA1c Level",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = hba1cLevel,
            onValueChange = { hba1cLevel = it },
            label = { Text("Enter HbA1c Level") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Blood Glucose Level
        Text(
            text = "Blood Glucose Level",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = bloodGlucoseLevel,
            onValueChange = { bloodGlucoseLevel = it },
            label = { Text("Enter Glucose Level") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

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
                    navController.navigate("result/$prediction")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Predict", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun DiabetesResultScreen() {
    // This would display your prediction result
    // Implement based on your needs
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedItem)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
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
    // Implement your validation logic here
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
    // One-Hot Encoding for gender
    val genderOneHot = when (gender) {
        "Male" -> floatArrayOf(1.0f, 0.0f)
        "Female" -> floatArrayOf(0.0f, 1.0f)
        else -> floatArrayOf(0.0f, 0.0f)
    }

    // One-Hot Encoding for smoking history
    val smokingHistoryOneHot = when (smokingHistory) {
        "Never" -> floatArrayOf(1.0f, 0.0f, 0.0f)
        "Current" -> floatArrayOf(0.0f, 1.0f, 0.0f)
        "Former" -> floatArrayOf(0.0f, 0.0f, 1.0f)
        else -> floatArrayOf(0.0f, 0.0f, 0.0f)
    }

    // Prepare byte buffer (8 features)
    val byteBuffer = ByteBuffer.allocateDirect(8 * 4).apply {
        order(ByteOrder.nativeOrder())
        // Gender
        putFloat(genderOneHot[0])
        putFloat(genderOneHot[1])
        // Smoking history
        putFloat(smokingHistoryOneHot[0])
        putFloat(smokingHistoryOneHot[1])
        putFloat(smokingHistoryOneHot[2])
        // Other features
        putFloat(age)
        putFloat(bmi)
        putFloat(bloodGlucoseLevel)
    }

    // Load model and make prediction
    val model = Model.newInstance(context)
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 8), DataType.FLOAT32)
    inputFeature0.loadBuffer(byteBuffer)

    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer
    val prediction = outputFeature0.floatArray[0]

    model.close()

    return prediction * 100 // Return as percentage
}