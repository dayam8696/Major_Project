package com.example.majorproject.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.majorproject.dataModel.HospitalElement
import com.example.majorproject.viewModel.HospitalViewModel

@Composable
fun HospitalListScreen(navController: NavController) {
    val context = LocalContext.current

    val viewModel: HospitalViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HospitalViewModel(context.applicationContext) as T
            }
        }
    )

    // Observe state
    val hospitals by remember { derivedStateOf { viewModel.hospitals } }
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val errorMessage by remember { derivedStateOf { viewModel.errorMessage } }

    // Animation states
    val fadeAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "fade_animation"
    )

    val slideAnim by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ),
        label = "slide_animation"
    )

    // Handle location permission and fetch hospitals
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val permissionResult = ContextCompat.checkSelfPermission(context, permission)

        if (permissionResult == PackageManager.PERMISSION_GRANTED) {
            Log.d("HospitalListScreen", "Permission granted, fetching hospitals")
            viewModel.fetchHospitals()
        } else {
            Log.d("HospitalListScreen", "Requesting location permission")
            val activity = context as? Activity
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(permission),
                    1001
                )
            }
        }
    }

    // Log for UI debugging
    LaunchedEffect(hospitals, isLoading) {
        Log.d("HospitalListScreen", "UI State - isLoading: $isLoading, Hospitals: ${hospitals.size}")
        if (!isLoading && hospitals.isNotEmpty()) {
            hospitals.forEach { hospital ->
                Log.d("HospitalListScreen", "UI Hospital: ${hospital.tags?.name}, Lat: ${hospital.lat}, Lon: ${hospital.lon}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFF0F7FF)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = slideAnim)
                .graphicsLayer(alpha = fadeAnim),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nearby Hospitals",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0D3B66)
                    )
                    Text(
                        text = "Discover healthcare options near you",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Content
            when {
                isLoading -> LoadingState()
                errorMessage != null -> ErrorState(
                    message = errorMessage!!,
                    onRetry = { viewModel.retryFetchHospitals() }
                )
                hospitals.isEmpty() -> EmptyState()
                else -> HospitalList(
                    hospitals = hospitals,
                    onHospitalClick = { hospital ->
                        Log.d("HospitalListScreen", "Clicked: ${hospital.tags?.name}")
                        // Example navigation (uncomment to use):
                        // navController.navigate("hospitalDetails/${hospital.id ?: hospital.hashCode()}")
                    }
                )
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pulsing animation for progress indicator
        val pulseAnim by animateFloatAsState(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_animation"
        )

        CircularProgressIndicator(
            modifier = Modifier
                .size(70.dp)
                .scale(pulseAnim),
            color = Color(0xFF0288D1),
            strokeWidth = 7.dp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Please wait, we are fetching hospitals based on your current location",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0D3B66),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No hospitals found nearby",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D3B66),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Try adjusting your location settings or check back later.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Oops, something went wrong",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D3B66),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedButton(
            text = "Retry",
            onClick = onRetry,
            gradientColors = listOf(Color(0xFF0288D1), Color(0xFF4FC3F7)),
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
        )
    }
}

@Composable
fun HospitalList(
    hospitals: List<HospitalElement>,
    onHospitalClick: (HospitalElement) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(hospitals, key = { it.id ?: it.hashCode() }) { hospital ->
            HospitalItem(
                hospital = hospital,
                onClick = { onHospitalClick(hospital) }
            )
        }
    }
}

@Composable
fun HospitalItem(
    hospital: HospitalElement,
    onClick: () -> Unit
) {
    // Staggered entrance animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val fadeAnim by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "item_fade"
    )

    val slideAnim by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 30.dp,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "item_slide"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "item_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .offset(x = slideAnim)
            .graphicsLayer(alpha = fadeAnim)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = hospital.tags?.name ?: "Unnamed Hospital",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D3B66)
            )
            Text(
                text = "Latitude: ${hospital.lat}, Longitude: ${hospital.lon}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            )
            Text(
                text = "Tap to view more details",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0288D1)
            )
        }
    }
}

@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 8.dp,
        animationSpec = tween(durationMillis = 200),
        label = "button_elevation"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .shadow(elevation, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}