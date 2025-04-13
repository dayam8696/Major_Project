import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Heart Attack Risk Prediction",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // All input fields
                InputFieldWithInfo("Age", age, { age = it }, {
                    dialogTitle = "Age"
                    dialogMessage = "Enter your age in years."
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Sex", sex, { sex = it }, {
                    dialogTitle = "Sex"
                    dialogMessage = "1 = Male, 0 = Female"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Chest Pain Type", cp, { cp = it }, {
                    dialogTitle = "Chest Pain Type"
                    dialogMessage = "0 = typical angina, 1 = atypical angina, 2 = non-anginal, 3 = asymptomatic"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Resting BP", trtbps, { trtbps = it }, {
                    dialogTitle = "Resting Blood Pressure"
                    dialogMessage = "Measured in mm Hg"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Cholesterol", chol, { chol = it }, {
                    dialogTitle = "Cholesterol"
                    dialogMessage = "mg/dl (normal < 200)"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Fasting Blood Sugar", fbs, { fbs = it }, {
                    dialogTitle = "Fasting Blood Sugar"
                    dialogMessage = "1 if >120 mg/dl, else 0"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Resting ECG", restecg, { restecg = it }, {
                    dialogTitle = "Resting ECG"
                    dialogMessage = "0 = normal, 1 = ST-T abnormality, 2 = LV hypertrophy"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Max Heart Rate", thalachh, { thalachh = it }, {
                    dialogTitle = "Max Heart Rate"
                    dialogMessage = "Your highest heart rate during test"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Exercise Induced Angina", exng, { exng = it }, {
                    dialogTitle = "Exercise Induced Angina"
                    dialogMessage = "1 = Yes, 0 = No"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("ST Depression", oldpeak, { oldpeak = it }, {
                    dialogTitle = "ST Depression"
                    dialogMessage = "ST depression induced by exercise"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Slope of ST", slp, { slp = it }, {
                    dialogTitle = "Slope"
                    dialogMessage = "0 = upsloping, 1 = flat, 2 = downsloping"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("No. of Major Vessels", caa, { caa = it }, {
                    dialogTitle = "Major Vessels"
                    dialogMessage = "0 to 3 colored by fluoroscopy"
                    showDialog = true
                }, KeyboardType.Number)

                InputFieldWithInfo("Thalassemia", thall, { thall = it }, {
                    dialogTitle = "Thalassemia"
                    dialogMessage = "0 = normal, 1 = fixed defect, 2 = reversible"
                    showDialog = true
                }, KeyboardType.Number)

                Spacer(modifier = Modifier.height(16.dp))

                val coroutineScope = rememberCoroutineScope()

                val context = LocalContext.current // ← move this here

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val inputs = getInputs(
                                    age, sex, cp, trtbps, chol, fbs, restecg,
                                    thalachh, exng, oldpeak, slp, caa, thall
                                )
                                val result = runModel(context, inputs) // use context here
                                navController.navigate("heartResult/${"%.2f".format(result)}")
                            } catch (e: Exception) {
                                dialogTitle = "Error Message"
                                dialogMessage = "Please enter all fields correctly and perfectly"
                                showDialog = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Predict Risk")
                }


            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(dialogTitle) },
            text = { Text(dialogMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
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
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = infoClick, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Info, contentDescription = "Info")
            }
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

// Converts inputs to float array for model
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

// Runs the TFLite model
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
