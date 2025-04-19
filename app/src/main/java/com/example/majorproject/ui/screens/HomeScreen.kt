package com.example.majorproject.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.majorproject.R

@Composable
fun HomeScreen(navController: NavController) {
    // State to control dialog visibility
    var showComingSoonDialog by remember { mutableStateOf(false) }

    // Animation states for fade-in and slide-in
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Health Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A3C6D)
                )
                AnimatedIconButton(
                    onClick = { /* Settings action */ },
                    iconRes = R.drawable.baseline_settings_24,
                    contentDescription = "Settings",
                    tint = Color(0xFF1976D2)
                )
            }

            // Favorite Tools Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Favorite Tools",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A3C6D)
                    )
                    FavoriteToolItem(
                        title = "Joint Analysis",
                        description = "Analyze your joint condition with AI",
                        imageRes = R.drawable.joint,
                        gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF81C784)),
                        onClick = { navController.navigate("kneePredictionScreen") }
                    )
                    FavoriteToolItem(
                        title = "Predict Disease",
                        description = "Use AI to predict diseases based on your health data",
                        imageRes = R.drawable.graph,
                        gradientColors = listOf(Color(0xFFFF9800), Color(0xFFFFB300)),
                        onClick = { navController.navigate("SelectDiseaseScreen") }
                    )
                }
            }

            // Health Data Section (Scrollable Horizontally)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Allow card to take remaining space
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Health Data",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A3C6D),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            listOf(
                                HealthDataModel(
                                    title = "Predict Disease",
                                    iconTint = Color(0xFFE91E63),
                                    gradientColors = listOf(Color(0xFFE91E63), Color(0xFFF06292)),
                                    onClick = { navController.navigate("SelectDiseaseScreen") }
                                ),
                                HealthDataModel(
                                    title = "Nearby Hospitals",
                                    iconTint = Color(0xFF2196F3),
                                    gradientColors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6)),
                                    onClick = { navController.navigate("HospitalListScreen") }
                                ),
                                HealthDataModel(
                                     title = "Emergency Contact",
                                    iconTint = Color(0xFF9C27B0),
                                    gradientColors = listOf(Color(0xFF9C27B0), Color(0xFFBA68C8)),
                                    onClick = { navController.navigate("EmergencyContactScreen") }
                                ),
                                HealthDataModel(
                                    title = "Inventory Management",
                                    iconTint = Color(0xFF009688),
                                    gradientColors = listOf(Color(0xFF009688), Color(0xFF4DB6AC)),
                                    onClick = { showComingSoonDialog = true }
                                )
                            )
                        ) { item ->
                            HealthDataItem(
                                title = item.title,
                                iconTint = item.iconTint,
                                gradientColors = item.gradientColors,
                                onClick = item.onClick
                            )
                        }
                    }
                }
            }
        }

        // Coming Soon Dialog
        if (showComingSoonDialog) {
            AlertDialog(
                onDismissRequest = { showComingSoonDialog = false },
                title = {
                    Text(
                        text = "Coming Soon",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A3C6D)
                    )
                },
                text = {
                    Text(
                        text = "This feature is under development and will be available soon!",
                        fontSize = 16.sp,
                        color = Color(0xFF1A3C6D)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { showComingSoonDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1976D2))
                    ) {
                        Text("OK")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(12.dp))
            )
        }
    }
}

data class HealthDataModel(
    val title: String,
    val iconTint: Color,
    val gradientColors: List<Color>,
    val onClick: () -> Unit
)

@Composable
fun AnimatedIconButton(
    onClick: () -> Unit,
    iconRes: Int,
    contentDescription: String,
    tint: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .scale(scale)
            .size(48.dp)
            .background(Color(0xFFE3F2FD), CircleShape)
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun FavoriteToolItem(
    title: String,
    description: String,
    imageRes: Int,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradientColors))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = gradientColors[0]
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .animateContentSize()
                ) {
                    Text(
                        text = "Start",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(2.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun HealthDataItem(
    title: String,
    iconTint: Color,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
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
            .width(200.dp) // Fixed width for each card in the row
            .height(120.dp) // Height matched to approximate FavoriteToolItem
            .scale(scale)
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column   (
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement  = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 17.sp, // Adjusted for readability
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
             Icon(
                 painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                 contentDescription = "Navigate",
                 tint = Color.White,
                 modifier = Modifier.size(24.dp)
             )

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = gradientColors[0]
                ),
                modifier = Modifier
                    .height(40.dp)
                    .animateContentSize()
            ) {
                Text(
                    text = "Start Now",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}