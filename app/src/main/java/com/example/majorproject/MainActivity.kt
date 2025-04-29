package com.example.majorproject

import HeartAttackPredictionScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.majorproject.repository.GeminiRepository
import com.example.majorproject.ui.MedicineReminderScreen
import com.example.majorproject.ui.screens.*
import com.example.majorproject.ui.theme.MajorProjectTheme
import com.example.majorproject.viewModel.DepartmentViewModel
import com.example.majorproject.viewModel.GeminiViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = GeminiRepository()
        val viewModelFactory = GeminiViewModelFactory(repository)

        setContent {
            MajorProjectTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        viewModelFactory = viewModelFactory
                    )
                }
            }
        }
    }
}

// Data class for bottom navigation items
data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: Int // Resource ID for the icon
)

// Bottom Navigation Bar Composable
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(
            route = "HomeScreen",
            title = "Home",
            icon = R.drawable.home
        ),
        BottomNavItem(
            route = "CustomQueryScreen",
            title = "AI Guidance",
            icon = R.drawable.lightbulb
        ),
        BottomNavItem(
            route = "MedicationAlarmScreen",
            title = "Reminder",
            icon = android.R.drawable.ic_menu_agenda
        ),
        BottomNavItem(
            route = "EmergencySOS",
            title = "SOS",
            icon = R.drawable.emergencycall
        )
    )

    NavigationBar(
        containerColor = Color(0xFF2584E1),
        contentColor = Color.White,
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.title, fontSize = 12.sp) },
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == "HomeScreen") {
                        // For HomeScreen, pop the entire back stack and navigate to HomeScreen
                        navController.navigate("HomeScreen") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    } else {
                        // For other destinations, navigate with popUpTo HomeScreen
                        navController.navigate(item.route) {
                            popUpTo("HomeScreen") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = Color.White.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModelFactory: GeminiViewModelFactory
) {
    NavHost(navController = navController, startDestination = "HomeScreen", modifier = modifier) {
        composable("kneePredictionScreen") {
            KneeSeverityPredictionScreen(navController)
        }
        composable("kneeHealthScreen") {
            KneeHealthScreen(navController)
        }
        composable("GeminiScreen") {
            GeminiScreen(viewModelFactory)
        }
        composable("HomeScreen") {
            HomeScreen(navController)
        }
        composable("MedicineReminderScreen") {
            MedicineReminderScreen(
                context = LocalContext.current,
                medicineName = "Medicine",
                hour = 15,
                minute = 58,
                navController = navController
            )
        }
        composable("HearthAttackPredictionScreen") {
            HeartAttackPredictionScreen(navController)
        }
        composable("HeartAttackResultScreen") {
            HeartAttackResultScreen(navController)
        }
        composable("DiabetesPredictionScreen") {
            DiabetesPredictionScreen(navController)
        }
        composable("DiabetesResultScreen") {
            DiabetesResultScreen(navController)
        }
        composable("SelectDiseaseScreen") {
            SelectDiseaseScreen(
                onDiabetesClick = { navController.navigate("DiabetesPredictionScreen") },
                onHeartClick = { navController.navigate("HearthAttackPredictionScreen") },
                navController
            )
        }
        composable("FindHospitalScreen") {
            FindHospitalScreen(navController)
        }
        composable("HospitalListScreen") {
            HospitalListScreen(navController)
        }
        composable("DepartmentScreen") {
            DepartmentScreen(viewModel = DepartmentViewModel(), navController)
        }
        composable(
            route = "doctor_list/{departmentName}",
            arguments = listOf(navArgument("departmentName") { type = NavType.StringType })
        ) { backStackEntry ->
            val departmentName = backStackEntry.arguments?.getString("departmentName") ?: ""
            DoctorListScreen(
                departmentName = departmentName,
                navController = navController,
                viewModel = DepartmentViewModel()
            )
        }
        composable(
            route = "opd_schedule/{regNo}",
            arguments = listOf(navArgument("regNo") { type = NavType.StringType })
        ) { backStackEntry ->
            val regNo = backStackEntry.arguments?.getString("regNo") ?: ""
            OpdScheduleScreen(
                regNo = regNo,
                navController = navController,
                viewModel = DepartmentViewModel()
            )
        }
        composable("EmergencyContactScreen") {
            EmergencyContactScreen(navController)
        }
        composable("CustomQueryScreen") {
            CustomQueryScreen(viewModelFactory, navController)
        }
        composable("DiabetesGeminiScreen") {
            DiabetesGeminiScreen(viewModelFactory, navController)
        }
        composable("EmergencySOS") {
            EmergencySOS(navController)
        }
        composable("MedicationAlarmScreen") {
            MedicationAlarmScreen(navController)
        }
        composable("CostPrediction") {
            CostPrediction(navController)
        }
        composable("CostCheckupDiagChooseScreen") {
            CostCheckupDiagChooseScreen(
                onDiabetesClick = { navController.navigate("MedicalCheckupScreen") },
                onHeartClick = { navController.navigate("CostPrediction") },
                navController
            )
        }
        composable("MedicalCheckupScreen") {
            MedicalCheckupScreen(navController)
        }
    }
}