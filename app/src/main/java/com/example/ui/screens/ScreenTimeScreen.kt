package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScreenTimeScreen(
    timeUsedSeconds: Long,
    limitMinutes: Int,
    onUpdateLimit: (Int) -> Unit,
    onSimulateLockScreen: () -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(limitMinutes.toFloat()) }
    val timeUsedMinutes = (timeUsedSeconds / 60).toInt()
    val remainingMinutes = (limitMinutes - timeUsedMinutes).coerceAtLeast(0)
    val isTimeExceeded = timeUsedMinutes >= limitMinutes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Screen Time Hero Gauge
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isTimeExceeded) Color(0xFF991B1B) else Color(0xFF0F172A)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TOTAL PHONE USAGE TODAY",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isTimeExceeded) Color(0xFFEF4444) else Color(0xFF10B981))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isTimeExceeded) "LIMIT EXCEEDED" else "${remainingMinutes}m LEFT",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val hours = timeUsedMinutes / 60
                val mins = timeUsedMinutes % 60
                val timeFormatted = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

                Text(
                    text = timeFormatted,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Timer Target: $limitMinutes Minutes Daily",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF99F6E4)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val progress = (timeUsedMinutes.toFloat() / limitMinutes.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (isTimeExceeded) Color(0xFFEF4444) else Color(0xFF10B981),
                    trackColor = Color(0xFF334155)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trigger Screen Lock Simulation Button
        Button(
            onClick = { onSimulateLockScreen() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("simulate_lock_screen_button")
        ) {
            Icon(Icons.Default.PhonelinkLock, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "TRIGGER SCREEN TIMER LOCK DEMO",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Configure Timer Slider Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SET DAILY SCREEN TIMER LIMIT",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "When time completes, screen automatically locks to prevent porn urge scrolling.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Timer Limit: ${sliderValue.toInt()} Minutes (${sliderValue.toInt() / 60}h ${sliderValue.toInt() % 60}m)",
                    fontWeight = FontWeight.Bold
                )

                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 15f..360f,
                    steps = 22, // 15 min increments
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                )

                // Quick Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(30, 60, 120, 180).forEach { minsPreset ->
                        OutlinedButton(
                            onClick = {
                                sliderValue = minsPreset.toFloat()
                                onUpdateLimit(minsPreset)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("${minsPreset}m")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onUpdateLimit(sliderValue.toInt()) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_screen_timer_button")
                ) {
                    Text("SAVE SCREEN TIMER LIMIT", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mindfulness Tips Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.SelfImprovement,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Self-Control Mastery Tip",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Setting a strict screen timer cuts late-night isolated browsing when urges are highest.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
