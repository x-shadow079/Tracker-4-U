package com.example.ui.notch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotchCounterOverlay(
    isVisible: Boolean,
    shortsCount: Int,
    reelsCount: Int,
    dailyLimit: Int,
    onIncrementShorts: () -> Unit,
    onIncrementReels: () -> Unit,
    onNotchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalShorts = shortsCount + reelsCount
    val isNearLimit = totalShorts >= dailyLimit

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(animationSpec = spring(stiffness = Spring.StiffnessLow)),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Camera Notch Floating Pill Shape
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F172A))
                    .border(
                        width = 1.5.dp,
                        color = if (isNearLimit) Color(0xFFEF4444) else Color(0xFF10B981),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { onNotchClick() }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("notch_counter_badge")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Pulse Icon
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isNearLimit) Color(0xFFEF4444) else Color(0xFF10B981))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        Icons.Default.SlowMotionVideo,
                        contentDescription = "Shorts Counter",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Notch Counter: $totalShorts / $dailyLimit Shorts",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Quick Increment Button on Notch
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { onIncrementShorts() }
                            .padding(4.dp)
                            .testTag("notch_quick_add_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Count Short",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
