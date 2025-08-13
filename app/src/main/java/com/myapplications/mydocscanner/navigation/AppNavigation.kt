package com.myapplications.mydocscanner.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myapplications.mydocscanner.screens.AboutScreen
import com.myapplications.mydocscanner.screens.ScannerScreen
import com.myapplications.mydocscanner.screens.StatusScreen
import com.myapplications.mydocscanner.viewmodel.QrViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val qrViewModel: QrViewModel = viewModel()

    // The start destination is now the StatusScreen.
    NavHost(navController = navController, startDestination = "status_screen") {
        composable("scanner_screen") {
            ScannerScreen(
                navController = navController,
                onCodeScanned = { item ->
                    qrViewModel.addItem(item)
                }
            )
        }
        composable("status_screen") {
            StatusScreen(navController = navController, viewModel = qrViewModel)
        }
        composable("about_screen") {
            AboutScreen(navController = navController)
        }
    }
}
