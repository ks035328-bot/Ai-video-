package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MahimaViewModel

data class HubAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: (MahimaViewModel, android.content.Context) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantHubScreen(viewModel: MahimaViewModel) {
    val context = LocalContext.current
    val flashlightOn by viewModel.flashlightOn.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()

    val actions = listOf(
        HubAction(
            title = if (flashlightOn) "Flashlight ON" else "Flashlight",
            subtitle = "Control device torch",
            icon = Icons.Default.FlashlightOn,
            onClick = { vm, ctx -> vm.toggleFlashlight(ctx) }
        ),
        HubAction(
            title = "Battery Status",
            subtitle = "Level: $batteryLevel",
            icon = Icons.Default.BatteryChargingFull,
            onClick = { vm, _ -> vm.checkBattery() }
        ),
        HubAction(
            title = "Set Alarm",
            subtitle = "Wake up reminders",
            icon = Icons.Default.Alarm,
            onClick = { vm, ctx -> vm.setAlarm(ctx, 7, 0) }
        ),
        HubAction(
            title = "Open Camera",
            subtitle = "Capture photos",
            icon = Icons.Default.CameraAlt,
            onClick = { vm, ctx -> vm.openCamera(ctx) }
        ),
        HubAction(
            title = "Play Music",
            subtitle = "Romantic playlist",
            icon = Icons.Default.MusicNote,
            onClick = { vm, ctx -> vm.playMusic(ctx) }
        ),
        HubAction(
            title = "Open WhatsApp",
            subtitle = "Chat with friends",
            icon = Icons.Default.Chat,
            onClick = { vm, ctx -> vm.openApp(ctx, "com.whatsapp") }
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Phone Control Hub ❤️", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                "Authorized Device Control & Mahima's Toolkit",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(actions) { action ->
                    Card(
                        onClick = { action.onClick(viewModel, context) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .height(130.dp)
                            .testTag("hub_card_${action.title}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.title,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    text = action.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = action.subtitle,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
