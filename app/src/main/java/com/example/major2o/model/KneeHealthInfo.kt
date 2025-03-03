package com.example.major2o.model

data class KneeHealthInfo(
    val grade: Int,
    val explanation: String,
    val symptoms: List<String>,
    val riskFactors: List<String>,
    val dos: List<String>,
    val donts: List<String>
)
