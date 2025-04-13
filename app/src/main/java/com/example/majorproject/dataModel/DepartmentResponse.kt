package com.example.majorproject.dataModel

data class DepartmentResponse(
    val Departments: List<Department>
)

data class Department(
    val name: String,
    val doctors: List<Doctor>? // Optional, as we're only using name for now
)

data class Doctor(
    val name: String,
    val designation: String,
    val reg_no: String,
    val phone: String?,
    val email: String,
    val expertise: List<String>,
    val area_of_interest: List<String>,
    val opd_schedule: OpdSchedule
)

data class OpdSchedule(
    val Monday: DaySchedule,
    val Tuesday: DaySchedule,
    val Wednesday: DaySchedule,
    val Thursday: DaySchedule,
    val Friday: DaySchedule,
    val Saturday: DaySchedule,
    val Sunday: DaySchedule,
    val specialty_clinic: String,
    val room_no: String? = null // Optional field for Orthopaedics
)

data class DaySchedule(
    val general_opd: String,
    val lunch: String? = null, // Optional field
    val specialty_clinic: String? = null // Optional field
)