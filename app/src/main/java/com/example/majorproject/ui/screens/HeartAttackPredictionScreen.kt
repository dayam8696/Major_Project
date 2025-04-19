import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import androidx.navigation.NavController
import com.example.majorproject.ml.HeartAttackPredictionModel
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
fun HeartAttackPredictionScreen(navController: NavController) {
    // State variables for inputs
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var cp by remember { mutableStateOf("") }
    var trtbps by remember { mutableStateOf("") }
    var chol by remember { mutableStateOf("") }
    var fbs by remember { mutableStateOf("") }
    var restecg by remember { mutableStateOf("") }
    var thalachh by remember { mutableStateOf("") }
    var exng by remember { mutableStateOf("") }
    var oldpeak by remember { mutableStateOf("") }
    var slp by remember { mutableStateOf("") }
    var caa by remember { mutableStateOf("") }
    var thall by remember { mutableStateOf("") }

    // Dialog state
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

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
                        text = "Heart Attack Risk Prediction",
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
                    InputFieldWithInfo(
                        label = "Age",
                        value = age,
                        onValueChange = { age = it },
                        infoClick = {
                            dialogTitle = "Age"
                            dialogMessage = "Enter your age in years (e.g., 45). This is your current age, which helps assess heart risk as older age can increase the likelihood of heart issues."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Sex",
                        value = sex,
                        onValueChange = { sex = it },
                        infoClick = {
                            dialogTitle = "Sex"
                            dialogMessage = "Enter 1 for Male or 0 for Female. Biological sex is used because heart attack risk can vary between males and females due to hormonal and physiological differences."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Chest Pain Type",
                        value = cp,
                        onValueChange = { cp = it },
                        infoClick = {
                            dialogTitle = "Chest Pain Type"
                            dialogMessage = "Select the type of chest pain:\n" +
                                    "0 = Typical angina (chest pain related to heart strain)\n" +
                                    "1 = Atypical angina (less specific chest discomfort)\n" +
                                    "2 = Non-anginal pain (not heart-related)\n" +
                                    "3 = Asymptomatic (no chest pain). " +
                                    "This helps evaluate if chest pain is linked to heart issues."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Resting BP",
                        value = trtbps,
                        onValueChange = { trtbps = it },
                        infoClick = {
                            dialogTitle = "Resting Blood Pressure"
                            dialogMessage = "Enter your resting blood pressure in mm Hg (e.g., 120 for 120/80 mm Hg). This is the pressure in your arteries when your heart is at rest. High values (e.g., above 140) may increase heart attack risk."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Cholesterol",
                        value = chol,
                        onValueChange = { chol = it },
                        infoClick = {
                            dialogTitle = "Cholesterol"
                            dialogMessage = "Enter your total cholesterol level in mg/dl (e.g., 180). Normal is below 200 mg/dl. High cholesterol can clog arteries, raising heart attack risk. Check your latest blood test results."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Fasting Blood Sugar",
                        value = fbs,
                        onValueChange = { fbs = it },
                        infoClick = {
                            dialogTitle = "Fasting Blood Sugar"
                            dialogMessage = "Enter 1 if your fasting blood sugar is above 120 mg/dl, or 0 if below. This measures blood sugar after not eating for 8 hours. High levels may indicate diabetes, a heart risk factor."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Resting ECG",
                        value = restecg,
                        onValueChange = { restecg = it },
                        infoClick = {
                            dialogTitle = "Resting ECG"
                            dialogMessage = "Select your resting electrocardiogram (ECG) result:\n" +
                                    "0 = Normal (no issues)\n" +
                                    "1 = ST-T wave abnormality (unusual heart activity)\n" +
                                    "2 = Left ventricular hypertrophy (thickened heart muscle). " +
                                    "This measures your heart’s electrical activity at rest. Consult your ECG report."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Max Heart Rate",
                        value = thalachh,
                        onValueChange = { thalachh = it },
                        infoClick = {
                            dialogTitle = "Max Heart Rate"
                            dialogMessage = "Enter the highest heart rate achieved during a stress test (e.g., 150 beats per minute). This shows how hard your heart can work. Lower values for your age may indicate higher risk. Check stress test results."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Exercise Induced Angina",
                        value = exng,
                        onValueChange = { exng = it },
                        infoClick = {
                            dialogTitle = "Exercise Induced Angina"
                            dialogMessage = "Enter 1 if you felt chest pain during exercise (like running or cycling), or 0 if not. This type of pain suggests your heart may not get enough blood during stress, increasing risk."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "ST Depression",
                        value = oldpeak,
                        onValueChange = { oldpeak = it },
                        infoClick = {
                            dialogTitle = "ST Depression"
                            dialogMessage = "Enter the ST depression value from an exercise ECG (e.g., 1.5 mm). This measures changes in your heart’s electrical activity during exercise. Higher values (e.g., above 1) may indicate heart strain. Check your stress test report."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Slope of ST",
                        value = slp,
                        onValueChange = { slp = it },
                        infoClick = {
                            dialogTitle = "Slope of ST"
                            dialogMessage = "Select the slope of the ST segment from an exercise ECG:\n" +
                                    "0 = Upsloping (normal recovery)\n" +
                                    "1 = Flat (possible heart issues)\n" +
                                    "2 = Downsloping (higher risk). " +
                                    "This shows how your heart recovers after exercise. Refer to your ECG report."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "No. of Major Vessels",
                        value = caa,
                        onValueChange = { caa = it },
                        infoClick = {
                            dialogTitle = "Number of Major Vessels"
                            dialogMessage = "Enter the number of major heart arteries (0 to 3) showing blockage, as seen in a fluoroscopy test. More blocked vessels (e.g., 2 or 3) increase heart attack risk. Check your angiogram results."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )

                    InputFieldWithInfo(
                        label = "Thalassemia",
                        value = thall,
                        onValueChange = { thall = it },
                        infoClick = {
                            dialogTitle = "Thalassemia"
                            dialogMessage = "Select your thalassemia test result:\n" +
                                    "0 = Normal (no issues)\n" +
                                    "1 = Fixed defect (permanent blood flow issue)\n" +
                                    "2 = Reversible defect (blood flow issue during stress). " +
                                    "This assesses blood flow to your heart. Consult your stress test or imaging results."
                            showDialog = true
                        },
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            // Predict Button
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val inputs = getInputs(
                                age, sex, cp, trtbps, chol, fbs, restecg,
                                thalachh, exng, oldpeak, slp, caa, thall
                            )
                            val result = runModel(context, inputs)
                            ResultHolders.predictedRisk = result
                            navController.navigate("HeartAttackResultScreen")

                            Log.d("HeartPrediction", "Predicted risk: ${"%.2f".format(result)}")
                            Toast.makeText(
                                context,
                                "Predicted risk: ${"%.2f".format(result)}",
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                            dialogTitle = "Error Message"
                            dialogMessage = "Please enter all fields correctly with valid numbers. Ensure you’ve provided accurate medical data for each parameter."
                            showDialog = true
                        }
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
                                    Color(0xFFD32F2F), // Red for heart theme
                                    Color(0xFFF44336)
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

        // Alert Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(dialogTitle, fontWeight = FontWeight.Bold, color = Color(0xFF1A3C6D)) },
                text = {
                    Text(
                        dialogMessage,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        lineHeight = 24.sp // Added for better readability
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1976D2))
                    ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Medium)
                }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun InputFieldWithInfo(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    infoClick: () -> Unit,
    keyboardType: KeyboardType
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A3C6D),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = infoClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color(0xFF1976D2)
                )
            }
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFD32F2F),
                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f),
                focusedLabelColor = Color(0xFFD32F2F),
                cursorColor = Color(0xFFD32F2F)
            )
        )
    }
}

// Logic remains unchanged
fun getInputs(
    age: String, sex: String, cp: String, trtbps: String, chol: String,
    fbs: String, restecg: String, thalachh: String, exng: String,
    oldpeak: String, slp: String, caa: String, thall: String
): FloatArray {
    return floatArrayOf(
        age.toFloat(), sex.toFloat(), cp.toFloat(), trtbps.toFloat(), chol.toFloat(),
        fbs.toFloat(), restecg.toFloat(), thalachh.toFloat(), exng.toFloat(),
        oldpeak.toFloat(), slp.toFloat(), caa.toFloat(), thall.toFloat()
    )
}

fun runModel(context: Context, inputData: FloatArray): Float {
    val model = HeartAttackPredictionModel.newInstance(context)

    val byteBuffer = ByteBuffer.allocateDirect(13 * 4)
    byteBuffer.order(ByteOrder.nativeOrder())
    inputData.forEach { byteBuffer.putFloat(it) }

    val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 13), DataType.FLOAT32)
    inputFeature.loadBuffer(byteBuffer)

    val output = model.process(inputFeature)
    val outputValue = output.outputFeature0AsTensorBuffer.floatArray[0]

    model.close()
    return outputValue
}

object ResultHolders {
    var predictedRisk: Float = 0.0f
}