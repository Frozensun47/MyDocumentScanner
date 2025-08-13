package com.myapplications.mydocscanner.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myapplications.mydocscanner.viewmodel.QrViewModel
import com.myapplications.mydocscanner.viewmodel.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(viewModel: QrViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("IN", "OUT")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> StatusList(
                    items = viewModel.inList,
                    status = Status.IN,
                    onSwitch = { item -> viewModel.switchItemStatus(item, Status.IN) }
                )
                1 -> StatusList(
                    items = viewModel.outList,
                    status = Status.OUT,
                    onSwitch = { item -> viewModel.switchItemStatus(item, Status.OUT) }
                )
            }
        }
    }
}

@Composable
fun StatusList(
    items: List<String>,
    status: Status,
    onSwitch: (String) -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No items in this list.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                StatusListItem(item = item, onSwitch = { onSwitch(item) })
            }
        }
    }
}

@Composable
fun StatusListItem(item: String, onSwitch: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = item, style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = onSwitch) {
                Icon(Icons.Default.SwapHoriz, contentDescription = "Switch Status")
            }
        }
    }
}
