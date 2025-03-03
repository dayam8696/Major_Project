package com.example.major2o.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.major2o.R
import com.example.major2o.databinding.KneeHealthFragmentBinding
import com.example.major2o.model.KneeHealthInfo

class KneeHealthFragment : Fragment() {
    private var _binding: KneeHealthFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = KneeHealthFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get prediction result from KneeSevertyPrediction
        val predictedGrade = KneeSevertyPrediction.kneeHealthPrediction

        // Map predictions to knee health information
        val kneeHealthMap = mapOf(
            "Grade 0: Normal knee - No signs of osteoarthritis. The knee joint appears healthy." to
                    KneeHealthInfo(0, "No osteoarthritis detected.", listOf("No symptoms"), listOf("None"), listOf("Stay active"), listOf("None")),
            "Grade 1: Very early changes - Minor joint space narrowing with possible small bone spurs." to
                    KneeHealthInfo(1, "Very early signs of osteoarthritis.", listOf("Minor discomfort"), listOf("Obesity", "Sedentary lifestyle"), listOf("Exercise regularly"), listOf("Avoid excess weight")),
            "Grade 2: Mild osteoarthritis - Definite bone spurs and possible joint space narrowing." to
                    KneeHealthInfo(2, "Moderate knee pain due to wear and tear.", listOf("Pain while walking", "Swelling", "Stiffness"), listOf("Obesity", "Lack of exercise", "Age over 50"), listOf("Exercise regularly", "Maintain healthy weight"), listOf("Avoid prolonged standing", "Do not ignore pain")),
            "Grade 3: Moderate osteoarthritis - Multiple bone spurs, clear joint space narrowing, and mild sclerosis." to
                    KneeHealthInfo(3, "Moderate osteoarthritis detected.", listOf("Significant knee pain", "Limited mobility"), listOf("Genetics", "Inactivity"), listOf("Use knee braces", "Consult a doctor"), listOf("Avoid high-impact sports")),
            "Grade 4: Severe osteoarthritis - Large bone spurs, significant joint narrowing, severe sclerosis, and possible deformity." to
                    KneeHealthInfo(4, "Severe osteoarthritis requiring medical attention.", listOf("Severe pain", "Joint deformity"), listOf("Age", "Repeated injuries"), listOf("Consider physiotherapy", "Possible surgery"), listOf("Avoid high-impact exercises", "Do not delay treatment"))
        )

        // Fetch knee health info or default to an unknown state
        val kneeHealthInfo = kneeHealthMap[predictedGrade] ?:
        KneeHealthInfo(-1, "Unknown result. Please try again.", listOf(), listOf(), listOf(), listOf())

        // Update UI
        binding.gradeTitle.text = getString(R.string.grade_text, kneeHealthInfo.grade)
        binding.explanation.text = kneeHealthInfo.explanation

        // Setup ListView adapters
        setupListView(binding.symptomsList, kneeHealthInfo.symptoms)
        setupListView(binding.riskFactorsList, kneeHealthInfo.riskFactors)
        setupListView(binding.dosList, kneeHealthInfo.dos)
        setupListView(binding.dontsList, kneeHealthInfo.donts)
    }

    // Helper function to set ListView adapters
    private fun setupListView(listView: ListView, items: List<String>) {
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
    }

}