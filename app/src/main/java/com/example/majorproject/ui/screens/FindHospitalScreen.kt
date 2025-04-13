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

    var selectedHospital by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

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

    // Glowing effect for button
    val glowAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_animation"
    )

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
                    .shadow(10.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Find a Hospital",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0D3B66)
                    )
                    Text(
                        text = "Discover top healthcare in Delhi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Input Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // City Display (Non-editable)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "City: Delhi",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0D3B66)
                            )
                        }
                    }

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
                                focusedIndicatorColor = Color(0xFF0288D1),
                                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f),
                                focusedLabelColor = Color(0xFF0288D1),
                                unfocusedLabelColor = Color.Gray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = Color(0xFF0288D1) // Fixed the error here
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
                                .shadow(6.dp, RoundedCornerShape(12.dp))
                                .fillMaxWidth()
                        ) {
                            hospitalList.forEachIndexed { index, hospital ->
                                // Staggered animation for dropdown items
                                val itemFade by animateFloatAsState(
                                    targetValue = if (expanded) 1f else 0f,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        delayMillis = index * 50,
                                        easing = FastOutSlowInEasing
                                    ),
                                    label = "item_fade_$index"
                                )

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            hospital,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF0D3B66),
                                            modifier = Modifier.graphicsLayer(alpha = itemFade)
                                        )
                                    },
                                    onClick = {
                                        selectedHospital = hospital
                                        expanded = false
                                        focusManager.clearFocus()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .background(
                                            if (selectedHospital == hospital) Color(0xFFE3F2FD)
                                            else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Proceed Button
            AnimatedButton(
                text = "Proceed",
                onClick = { /* Handle submit or navigation */ },
                gradientColors = listOf(Color(0xFF0288D1), Color(0xFF4FC3F7)),
                glowAlpha = glowAnim,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(60.dp)
            )
        }
    }
}

@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    gradientColors: List<Color>,
    glowAlpha: Float,
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
        targetValue = if (isPressed) 6.dp else 10.dp,
        animationSpec = tween(durationMillis = 200),
        label = "button_elevation"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0xFF0288D1).copy(alpha = glowAlpha * 0.5f),
                spotColor = Color(0xFF0288D1).copy(alpha = glowAlpha * 0.5f)
            )
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}