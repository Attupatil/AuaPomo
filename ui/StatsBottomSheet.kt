package com.patilgames.aurapomo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsBottomSheet(statsViewModel: StatsViewModel, goalViewModel: GoalViewModel, onDismiss: () -> Unit) {
    val sessions by statsViewModel.sessions.collectAsStateWithLifecycle()
    val streak by statsViewModel.streak.collectAsStateWithLifecycle()
    val reminders by goalViewModel.reminders.collectAsStateWithLifecycle(emptyList())
    var selectedTab by remember { mutableStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 16.dp)) {
                    Icon(Icons.Default.LocalFireDepartment, null, tint = Color(0xFFE07A5F), modifier = Modifier.size(48.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("$streak Day Streak", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Consistency leads to mastery.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 0.dp, containerColor = Color.Transparent) {
                    listOf("Today", "Week", "Month").forEachIndexed { index, title ->
                        Tab(selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                    }
                }
                Spacer(Modifier.height(16.dp))
                
                val displayData = when(selectedTab) {
                    0 -> statsViewModel.getMinutesForRange("Today", sessions)
                    1 -> statsViewModel.getMinutesForRange("Week", sessions)
                    else -> emptyList() // Month implementation placeholder
                }
                
                AnalyticsBarGraph(displayData)
                Spacer(Modifier.height(24.dp))
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Daily Reminders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Button(onClick = { showTimePicker = true }, shape = RoundedCornerShape(8.dp)) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add")
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            items(reminders) { reminder ->
                ListItem(
                    headlineContent = { Text(reminder.taskName) },
                    supportingContent = { Text("Daily at ${reminder.reminderHour}:${reminder.reminderMinute}") },
                    trailingContent = { 
                        IconButton(onClick = { goalViewModel.deleteReminder(reminder) }) {
                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(vertical = 4.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                )
            }
            
            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    if (showTimePicker) {
        ReminderDialog(onDismiss = { showTimePicker = false }) { name, h, m ->
            goalViewModel.addReminder(name, h, m, 1f)
            showTimePicker = false
        }
    }
}
