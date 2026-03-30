package com.example.kegelexercise.ui.screen.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.kegelexercise.R
import com.example.kegelexercise.service.TimerService
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()

    var durationSlider by remember(prefs.durationMinutes) {
        mutableFloatStateOf(prefs.durationMinutes.toFloat())
    }
    var tightenSlider by remember(prefs.tightenSeconds) {
        mutableFloatStateOf(prefs.tightenSeconds.toFloat())
    }
    var relaxSlider by remember(prefs.relaxSeconds) {
        mutableFloatStateOf(prefs.relaxSeconds.toFloat())
    }
    var vibrationSlider by remember(prefs.vibrationLevel) {
        mutableFloatStateOf(prefs.vibrationLevel.toFloat())
    }
    var showVibrationDialog by remember { mutableStateOf(false) }

    if (showVibrationDialog) {
        AlertDialog(
            onDismissRequest = { showVibrationDialog = false },
            title = { Text(stringResource(R.string.btn_vibration_settings)) },
            text = {
                SettingsCard(
                    title = stringResource(R.string.setting_vibration),
                    value = "${vibrationSlider.roundToInt()} / 10",
                    sliderValue = vibrationSlider,
                    valueRange = 1f..10f,
                    onValueChange = { vibrationSlider = it }
                )
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showVibrationDialog = false }) {
                    Text(stringResource(R.string.btn_close))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showVibrationDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.btn_vibration_settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = "home",
                onHomeClick = {},
                onHistoryClick = { navController.navigate("history") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            SettingsCard(
                title = stringResource(R.string.setting_duration),
                value = "${durationSlider.roundToInt()} ${stringResource(R.string.unit_minutes)}",
                sliderValue = durationSlider,
                valueRange = 1f..60f,
                onValueChange = { durationSlider = it }
            )

            SettingsCard(
                title = stringResource(R.string.setting_tighten),
                value = "${tightenSlider.roundToInt()} ${stringResource(R.string.unit_seconds)}",
                sliderValue = tightenSlider,
                valueRange = 1f..30f,
                onValueChange = { tightenSlider = it }
            )

            SettingsCard(
                title = stringResource(R.string.setting_relax),
                value = "${relaxSlider.roundToInt()} ${stringResource(R.string.unit_seconds)}",
                sliderValue = relaxSlider,
                valueRange = 1f..30f,
                onValueChange = { relaxSlider = it }
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val duration = durationSlider.roundToInt()
                    val tighten = tightenSlider.roundToInt()
                    val relax = relaxSlider.roundToInt()
                    val vibration = vibrationSlider.roundToInt()
                    viewModel.savePreferences(duration, tighten, relax, vibration)
                    startTimerService(context, duration, tighten, relax, vibration)
                    navController.navigate("timer")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.padding(4.dp))
                Text(
                    text = stringResource(R.string.btn_start),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    value: String,
    sliderValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = sliderValue,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = (valueRange.endInclusive - valueRange.start).toInt() - 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
            label = { Text("運動") }
        )
        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = onHistoryClick,
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text("紀錄") }
        )
    }
}

private fun startTimerService(
    context: Context,
    durationMin: Int,
    tightenSec: Int,
    relaxSec: Int,
    vibrationLevel: Int
) {
    val intent = Intent(context, TimerService::class.java).apply {
        action = TimerService.ACTION_START
        putExtra(TimerService.EXTRA_DURATION_MIN, durationMin)
        putExtra(TimerService.EXTRA_TIGHTEN_SEC, tightenSec)
        putExtra(TimerService.EXTRA_RELAX_SEC, relaxSec)
        putExtra(TimerService.EXTRA_VIBRATION_LEVEL, vibrationLevel)
    }
    context.startForegroundService(intent)
}
