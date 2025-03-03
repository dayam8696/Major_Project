package com.example.major2o.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.major2o.R
import com.example.major2o.databinding.HomeFragmentBinding

class HomeFragment:BaseFragment() {
    private val binding by lazy { HomeFragmentBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root;

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnJointAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_kneeSevertyPrediction2)
        }
        binding.btnMedicineReminder.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mediceneReminder)
        }
    }
}