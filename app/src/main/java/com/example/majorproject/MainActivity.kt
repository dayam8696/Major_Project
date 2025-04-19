package com.example.majorproject

import HeartAttackPredictionScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
            icon = android.R.drawable.ic_menu_today // Replace with your own icon
        ),
        BottomNavItem(
            route = "CustomQueryScreen",
            title = "AI Guidance",
            icon = android.R.drawable.ic_menu_info_details // Replace with your own icon
        ),
        BottomNavItem(
            route = "MedicineReminderScreen",
            title = "Reminder",
            icon = android.R.drawable.ic_menu_agenda // Replace with your own icon
        ),
        BottomNavItem(
            route = "SelectDiseaseScreen",
            title = "Predict",
            icon = android.R.drawable.ic_menu_search // Replace with your own icon
        )
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination to avoid stacking screens
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple instances of the same screen
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
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
            CustomQueryScreen(viewModelFactory ,navController)
        }
        composable("DiabetesGeminiScreen") {
           DiabetesGeminiScreen(viewModelFactory ,navController)
        }
    }
}