package com.example.major2o.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.major2o.R
import com.example.major2o.databinding.KneeSeverrtyPredictionFragmentBinding
import com.example.major2o.ml.ModelFloat32
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
class KneeSevertyPrediction : Fragment() {
    private var _binding: KneeSeverrtyPredictionFragmentBinding? = null
    private val binding get() = _binding!!

    private var selectedBitmap: Bitmap? = null

    companion object {
        var kneeHealthPrediction: String? = null
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                if (imageUri != null) {
                    selectedBitmap = uriToBitmap(imageUri)
                    binding.imageView.setImageBitmap(selectedBitmap)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = KneeSeverrtyPredictionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLearnMore.setOnClickListener {
            findNavController().navigate(R.id.action_kneeSevertyPrediction2_to_kneeHealthFragment)
        }

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.btnPredict.setOnClickListener {
            selectedBitmap?.let { bitmap ->
                val prediction = classifyImage(bitmap)
                kneeHealthPrediction = prediction  // Store result in companion object
                binding.tvResult.text = "Prediction: $prediction"
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun classifyImage(bitmap: Bitmap): String {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

        val model = ModelFloat32.newInstance(requireContext())
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        model.close()

        return getSeverityDescription(argMax(outputFeature0.floatArray))
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        bitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)

        var pixel = 0
        for (i in 0 until 224) {
            for (j in 0 until 224) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f) // Red
                byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)  // Green
                byteBuffer.putFloat((value and 0xFF) / 255.0f)         // Blue
            }
        }
        return byteBuffer
    }

    private fun argMax(array: FloatArray): Int {
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

    private fun getSeverityDescription(grade: Int): String {
        return when (grade) {
            0 -> "Grade 0: Normal knee - No signs of osteoarthritis. The knee joint appears healthy."
            1 -> "Grade 1: Very early changes - Minor joint space narrowing with possible small bone spurs."
            2 -> "Grade 2: Mild osteoarthritis - Definite bone spurs and possible joint space narrowing."
            3 -> "Grade 3: Moderate osteoarthritis - Multiple bone spurs, clear joint space narrowing, and mild sclerosis."
            4 -> "Grade 4: Severe osteoarthritis - Large bone spurs, significant joint narrowing, severe sclerosis, and possible deformity."
            else -> "Unknown grade - Please try again with a clearer image."
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
