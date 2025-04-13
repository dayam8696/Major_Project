package com.example.majorproject


import HeartAttackPredictionScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.majorproject.repository.GeminiRepository
import com.example.majorproject.ui.screens.GeminiScreen
import com.example.majorproject.ui.screens.HomeScreen
import com.example.majorproject.ui.screens.KneeHealthScreen
import com.example.majorproject.ui.screens.KneeSeverityPredictionScreen
import com.example.majorproject.ui.MedicineReminderScreen
import com.example.majorproject.ui.screens.DiabetesPredictionScreen
import com.example.majorproject.ui.screens.DiabetesResultScreen
import com.example.majorproject.ui.screens.HeartAttackResultScreen
import com.example.majorproject.ui.screens.SelectDiseaseScreen
import com.example.majorproject.ui.theme.MajorProjectTheme
import com.example.majorproject.viewModel.GeminiViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = GeminiRepository()
        val viewModelFactory = GeminiViewModelFactory(repository)



        setContent {

            val navController = rememberNavController()
            val context = this
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AppNavHost(navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    viewModelFactory = viewModelFactory,

                )

            }

        }
    }
}


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier, viewModelFactory: GeminiViewModelFactory) {
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
                context = LocalContext.current, // Pass context correctly
                medicineName = "Medicine",  // Change this dynamically
                hour = 15,  // Set this dynamically
                minute = 58,  // Set this dynamically
                navController = navController
            )
        }
        composable(route = "HearthAttackPredictionScreen"){
           HeartAttackPredictionScreen(navController)
        }

        composable(route = "HeartAttackResultScreen"){
            HeartAttackResultScreen(navController)
        }
        composable(route = "DiabetesPredictionScreen") {
            DiabetesPredictionScreen(navController)
        }
        composable(route = "DiabetesResultScreen") {
            DiabetesResultScreen(navController)
        }
        composable(route = "SelectDiseaseScreen") {
            SelectDiseaseScreen(
                onDiabetesClick = {
                    navController.navigate("DiabetesPredictionScreen")
                },
                onHeartClick = {
                    navController.navigate("HearthAttackPredictionScreen")
                },
                 navController
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MajorProjectTheme {
        Greeting("Android")
    }
}