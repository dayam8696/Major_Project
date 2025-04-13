package com.example.majorproject.dataModel

data class HospitalResponse(
    val elements: List<HospitalElement>
)

data class HospitalElement(
    val type: String,
    val id: Long,
    val lat: Double?,
    val lon: Double?,
    val tags: Tags?
)

data class Tags(
    val name: String?
)

