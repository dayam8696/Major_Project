package com.example.majorproject.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.majorproject.ml.ModelFloat32
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
fun KneeSeverityPredictionScreen(navController: NavController) {


    val context = LocalContext.current
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var predictionResult by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                selectedBitmap = uriToBitmap(context, imageUri)
                predictionResult = null // Reset result when new image is selected
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Knee Severity Prediction",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(16.dp))

                selectedBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                    )
                } ?: Text(
                    text = "No Image Selected",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        imagePickerLauncher.launch(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Select Image", fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedBitmap?.let {
                            isLoading = true
                            val result = classifyImage(context, it)
                            PredictionManager.predictionResult = result  // Store in companion object
                            predictionResult = result  // Also update UI state
                            isLoading = false
                        }
                    },
                    enabled = selectedBitmap != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Predict", fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFF6200EA))
                }

                predictionResult?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Learn More Button
                Button(
                  //  onClick = { navController.navigate("kneeHealthScreen") },
                    onClick = { navController.navigate("GeminiScreen") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Learn More", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun classifyImage(context: Context, bitmap: Bitmap): String {
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
    val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

    val model = ModelFloat32.newInstance(context)
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
    inputFeature0.loadBuffer(byteBuffer)

    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

    model.close()

    return getSeverityDescription(argMax(outputFeature0.floatArray))
}

fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
    val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
    byteBuffer.order(ByteOrder.nativeOrder())

    val intValues = IntArray(224 * 224)
    bitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)

    var pixel = 0
    for (i in 0 until 224) {
        for (j in 0 until 224) {
            val value = intValues[pixel++]
            byteBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f)   // Red
            byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)    // Green
            byteBuffer.putFloat((value and 0xFF) / 255.0f)            // Blue
        }
    }
    return byteBuffer
}

fun argMax(array: FloatArray): Int {
    var maxIndex = 0
    var maxValue = array[0]
    for (i in array.indices) {
        if (array[i] > maxValue) {
            maxValue = array[i]
            maxIndex = i
        }
    }
    return maxIndex
}

fun getSeverityDescription(grade: Int): String {
    return when (grade) {
        0 -> "Grade 0: Normal knee - No signs of osteoarthritis."
        1 -> "Grade 1: Very early changes - Minor joint space narrowing."
        2 -> "Grade 2: Mild osteoarthritis - Definite bone spurs."
        3 -> "Grade 3: Moderate osteoarthritis - Multiple bone spurs."
        4 -> "Grade 4: Severe osteoarthritis - Large bone spurs and deformity."
        else -> "Unknown grade - Please try again with a clearer image."
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewKneeSeverityPredictionScreen() {
    KneeSeverityPredictionScreen(navController = NavController(LocalContext.current))
}
class PredictionManager {
    companion object {
        var predictionResult: String? = null
    }
}
