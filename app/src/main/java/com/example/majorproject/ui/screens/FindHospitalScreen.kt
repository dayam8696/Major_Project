package com.example.majorproject.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindHospitalScreen(navController: NavController) {
    val hospitalList = listOf(
        "AIIMS Hospital", "Fortis Hospital", "Max Super Speciality Hospital",
        "Sir Ganga Ram Hospital", "BLK-Max Hospital", "Indraprastha Apollo Hospital",
        "Safdarjung Hospital", "Ram Manohar Lohia Hospital", "Holy Family Hospital",
        "Venkateshwar Hospital"
    )

    var city by remember { mutableStateOf("Delhi") }
    var selectedHospital by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Animation states
    val fadeAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "fade_animation"
    )

    val slideAnim by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "slide_animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE6F0FA),
                        Color(0xFFF5F9FF)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = slideAnim)
                .graphicsLayer(alpha = fadeAnim)
                .animateContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Find a Hospital",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A3C6D)
                    )
                    Text(
                        text = "Locate the best healthcare facilities",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Input Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // City TextField
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF1976D2),
                            unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFF1976D2),
                            cursorColor = Color(0xFF1976D2)
                        )
                    )

                    // Hospital Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedHospital,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Hospital") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f),
                                focusedLabelColor = Color(0xFF1976D2),
                                cursorColor = Color(0xFF1976D2)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(Color.White)
                                .shadow(4.dp, RoundedCornerShape(8.dp))
                        ) {
                            hospitalList.forEach { hospital ->
                                DropdownMenuItem(
                                    text = { Text(hospital, fontSize = 16.sp, color = Color(0xFF1A3C6D)) },
                                    onClick = {
                                        selectedHospital = hospital
                                        expanded = false
                                        focusManager.clearFocus()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Proceed Button
            AnimatedButton(
                text = "Proceed",
                onClick = { /* Handle submit or navigation */ },
                gradientColors = listOf(Color(0xFF1976D2), Color(0xFF42A5F5)),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
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