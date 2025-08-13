package com.myapplications.mydocscanner.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.myapplications.mydocscanner.R
import com.myapplications.mydocscanner.model.ScanItem
import com.myapplications.mydocscanner.screens.components.*
import com.myapplications.mydocscanner.viewmodel.QrViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StatusScreen(navController: NavController, viewModel: QrViewModel) {
    val tabs = listOf("IN", "OUT")
    // Use the new rememberPagerState from androidx.compose.foundation.pager
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showConfirmSwitchDialog by remember { mutableStateOf<ScanItem?>(null) }
    var showEditDialog by remember { mutableStateOf<ScanItem?>(null) }
    var showLogDialog by remember { mutableStateOf<ScanItem?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteDialog by remember { mutableStateOf<ScanItem?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("MyDocScanner", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                navController.navigate("about_screen")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete All Data") },
                            onClick = {
                                showDeleteAllDialog = true
                                showMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("scanner_screen") }) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Search by Content") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Use the new HorizontalPager from androidx.compose.foundation.pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val listToShow = when (page) {
                    0 -> viewModel.inList
                    else -> viewModel.outList
                }

                val filteredList = listToShow.filter {
                    it.content.contains(searchQuery, ignoreCase = true)
                }

                StatusList(
                    items = filteredList,
                    onSwitch = { item -> showConfirmSwitchDialog = item },
                    onEdit = { item -> showEditDialog = item },
                    onViewLogs = { item -> showLogDialog = item },
                    onDelete = { item -> showConfirmDeleteDialog = item }
                )
            }
        }
    }

    // --- Dialogs ---

    showConfirmSwitchDialog?.let { item ->
        ConfirmationDialog(
            title = "Confirm Switch",
            text = "Are you sure you want to switch the status of '${item.content}'?",
            onConfirm = {
                viewModel.switchItemStatus(item)
                showConfirmSwitchDialog = null
            },
            onDismiss = { showConfirmSwitchDialog = null }
        )
    }

    showEditDialog?.let { item ->
        EditItemDialog(
            item = item,
            onSave = { updatedContent, updatedNotes ->
                viewModel.updateItem(item, updatedContent, updatedNotes)
                showEditDialog = null
            },
            onDismiss = { showEditDialog = null }
        )
    }

    showLogDialog?.let { item ->
        LogDialog(item = item, onDismiss = { showLogDialog = null })
    }

    if (showDeleteAllDialog) {
        DeleteDataDialog(
            onDismiss = { showDeleteAllDialog = false },
            onConfirm = {
                viewModel.deleteAllData()
                showDeleteAllDialog = false
            }
        )
    }

    showConfirmDeleteDialog?.let { item ->
        ConfirmationDialog(
            title = "Confirm Deletion",
            text = "Are you sure you want to permanently delete '${item.content}'?",
            onConfirm = {
                viewModel.deleteItem(item)
                showConfirmDeleteDialog = null
            },
            onDismiss = { showConfirmDeleteDialog = null }
        )
    }
}

@Composable
fun StatusList(
    items: List<ScanItem>,
    onSwitch: (ScanItem) -> Unit,
    onEdit: (ScanItem) -> Unit,
    onViewLogs: (ScanItem) -> Unit,
    onDelete: (ScanItem) -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No items found.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { item ->
                StatusListItem(
                    item = item,
                    onSwitch = onSwitch,
                    onEdit = onEdit,
                    onViewLogs = onViewLogs,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
fun StatusListItem(
    item: ScanItem,
    onSwitch: (ScanItem) -> Unit,
    onEdit: (ScanItem) -> Unit,
    onViewLogs: (ScanItem) -> Unit,
    onDelete: (ScanItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.content, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Last updated: ${dateFormat.format(item.lastUpdated)}", style = MaterialTheme.typography.bodySmall)
                }
                Box {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text("Switch Status") }, onClick = { onSwitch(item); showMenu = false })
                        DropdownMenuItem(text = { Text("Edit/Add Note") }, onClick = { onEdit(item); showMenu = false })
                        DropdownMenuItem(text = { Text("View Logs") }, onClick = { onViewLogs(item); showMenu = false })
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = { onDelete(item); showMenu = false }
                        )
                    }
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                    Text("Notes:", fontWeight = FontWeight.Bold)
                    Text(
                        text = item.notes.ifEmpty { "No notes added." },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
