package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.db.BlockedDomainEntity

@Composable
fun BlockerScreen(
    isShieldActive: Boolean,
    blockedDomains: List<BlockedDomainEntity>,
    onToggleShield: (Boolean) -> Unit,
    onTestUrl: suspend (String) -> Boolean,
    onAddCustomDomain: (String) -> Unit,
    onRemoveDomain: (String) -> Unit
) {
    var urlTestInput by remember { mutableStateOf("") }
    var testResult by remember { mutableStateOf<Boolean?>(null) }
    var customDomainInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Shield Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isShieldActive) Color(0xFF0F766E) else Color(0xFF334155)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isShieldActive) Color(0xFF10B981) else Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (isShieldActive) "ADULT BLOCKER ACTIVE" else "SHIELD PAUSED",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (isShieldActive) "Auto-blocking xhamstar, pornhub & 18+ sites" else "Tap toggle to re-activate protection",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCCFBF1)
                        )
                    }
                }

                Switch(
                    checked = isShieldActive,
                    onCheckedChange = { onToggleShield(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF10B981)
                    ),
                    modifier = Modifier.testTag("shield_active_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Interactive URL Checker Tool
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "URL CHECKER & TESTER",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Test any site (e.g., 'pornhub.com', 'xhamster', 'wikipedia.org'):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = urlTestInput,
                        onValueChange = {
                            urlTestInput = it
                            testResult = null
                        },
                        placeholder = { Text("e.g. pornhub.com or xhamstar") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("url_checker_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (urlTestInput.isNotBlank()) {
                                // Simulate coroutine check
                                val testDomain = urlTestInput.lowercase().trim()
                                val isBlocked = testDomain.contains("pornhub") || testDomain.contains("xhamst") ||
                                        testDomain.contains("xvideo") || testDomain.contains("redtube") ||
                                        testDomain.contains("xxx") || testDomain.contains("porn") ||
                                        testDomain.contains("youporn") || testDomain.contains("brazzers") ||
                                        blockedDomains.any { it.domain.contains(testDomain) }
                                testResult = isBlocked
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("url_checker_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Test")
                    }
                }

                testResult?.let { blocked ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (blocked) Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f))
                            .border(
                                width = 1.dp,
                                color = if (blocked) Color(0xFFEF4444) else Color(0xFF10B981),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (blocked) Icons.Default.Block else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (blocked) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (blocked) "🛑 BLOCKED BY SHIELD! (Adult/18+ Filter)" else "✅ ALLOWED WEBSITE",
                                fontWeight = FontWeight.Bold,
                                color = if (blocked) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Add Custom Domain Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customDomainInput,
                onValueChange = { customDomainInput = it },
                label = { Text("Add Custom Domain to Block") },
                placeholder = { Text("domain.com") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("custom_domain_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (customDomainInput.isNotBlank()) {
                        onAddCustomDomain(customDomainInput)
                        customDomainInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                modifier = Modifier.testTag("add_custom_domain_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Blocked Domains List
        Text(
            text = "AUTO-BLOCKED ADULT DOMAIN LIST (${blockedDomains.size})",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(blockedDomains) { domainEntity ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Public,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = domainEntity.domain,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${domainEntity.category} • Blocked ${domainEntity.timesBlocked}x",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        if (domainEntity.isCustom) {
                            IconButton(onClick = { onRemoveDomain(domainEntity.domain) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
