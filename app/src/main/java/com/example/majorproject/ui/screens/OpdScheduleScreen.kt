package com.example.majorproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.majorproject.dataModel.DaySchedule
import com.example.majorproject.dataModel.Doctor
import com.example.majorproject.dataModel.OpdSchedule
import com.example.majorproject.viewModel.DepartmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpdScheduleScreen(
    regNo: String,
    navController: NavController,
    viewModel: DepartmentViewModel = viewModel()
) {
    val departments by viewModel.departmentsFlow.collectAsState()

    // Show progress bar while loading data
    if (departments.isEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Loading OPD Schedule...",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        )
        return
    }

    // Find the doctor by reg_no
    val doctor: Doctor? = departments
        .flatMap { it.doctors ?: emptyList() }
        .find { it.reg_no == regNo }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${doctor?.name ?: "Doctor"}'s OPD Schedule",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            if (doctor == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Doctor not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                OpdScheduleContent(
                    opdSchedule = doctor.opd_schedule,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    )
}

@Composable
fun OpdScheduleContent(opdSchedule: OpdSchedule, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Weekly Schedule",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
        // List of days and their schedules
        items(
            listOf(
                "Monday" to opdSchedule.Monday,
                "Tuesday" to opdSchedule.Tuesday,
                "Wednesday" to opdSchedule.Wednesday,
                "Thursday" to opdSchedule.Thursday,
                "Friday" to opdSchedule.Friday,
                "Saturday" to opdSchedule.Saturday,
                "Sunday" to opdSchedule.Sunday
            )
        ) { (day, schedule) ->
            ScheduleItem(day = day, schedule = schedule)
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Specialty Clinic: ${opdSchedule.specialty_clinic}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    opdSchedule.room_no?.let { room ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Room No: $room",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleItem(day: String, schedule: DaySchedule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "General OPD: ${schedule.general_opd}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            schedule.lunch?.let { lunch ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lunch: $lunch",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            schedule.specialty_clinic?.let { clinic ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Specialty Clinic: $clinic",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
