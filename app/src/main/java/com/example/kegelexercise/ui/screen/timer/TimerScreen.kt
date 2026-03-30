package com.example.kegelexercise.ui.screen.timer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.kegelexercise.R
import com.example.kegelexercise.TimerPhase
import com.example.kegelexercise.ui.theme.CompletedColor
import com.example.kegelexercise.ui.theme.RelaxColor
import com.example.kegelexercise.ui.theme.TightenColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    navController: NavController,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.timerState.collectAsStateWithLifecycle()
    var showStopDialog by remember { mutableStateOf(false) }

    // Navigate back to home when session completes
    LaunchedEffect(state.phase) {
        if (state.phase == TimerPhase.COMPLETED) {
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    BackHandler {
        showStopDialog = true
    }

    val phaseColor by animateColorAsState(
        targetValue = when (state.phase) {
            TimerPhase.TIGHTEN -> TightenColor
            TimerPhase.RELAX -> RelaxColor
            TimerPhase.COMPLETED -> CompletedColor
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(durationMillis = 500),
        label = "phaseColor"
    )

    val phaseProgress by animateFloatAsState(
        targetValue = if (state.phase == TimerPhase.TIGHTEN || state.phase == TimerPhase.RELAX) {
            val total = if (state.phase == TimerPhase.TIGHTEN) state.tightenSeconds else state.relaxSeconds
            if (total > 0) state.phaseRemainingSeconds.toFloat() / total.toFloat() else 0f
        } else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "phaseProgress"
    )

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text(stringResource(R.string.dialog_stop_title)) },
            text = { Text(stringResource(R.string.dialog_stop_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showStopDialog = false
                        viewModel.stopTimer(context)
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.dialog_stop_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) {
                    Text(stringResource(R.string.dialog_stop_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.screen_timer)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = phaseColor.copy(alpha = 0.15f)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showStopDialog = true },
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = stringResource(R.string.btn_stop),
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Phase label
            Text(
                text = when (state.phase) {
                    TimerPhase.TIGHTEN -> stringResource(R.string.phase_tighten)
                    TimerPhase.RELAX -> stringResource(R.string.phase_relax)
                    else -> ""
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = phaseColor
            )

            Spacer(Modifier.height(32.dp))

            // Circular phase countdown
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { phaseProgress },
                    modifier = Modifier.size(220.dp),
                    color = phaseColor,
                    strokeWidth = 12.dp,
                    trackColor = phaseColor.copy(alpha = 0.15f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTime(state.phaseRemainingSeconds),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = phaseColor
                    )
                    Text(
                        text = stringResource(R.string.label_phase_remaining),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Total time remaining
            Text(
                text = stringResource(
                    R.string.label_total_remaining,
                    formatTime(state.totalRemainingSeconds)
                ),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(12.dp))

            // Cycle counter
            Text(
                text = stringResource(R.string.label_cycle, state.currentCycle),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTime(seconds: Int): String =
    "%02d:%02d".format(seconds / 60, seconds % 60)
