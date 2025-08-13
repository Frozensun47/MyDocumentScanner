package com.myapplications.mydocscanner.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myapplications.mydocscanner.screens.ScannerScreen
import com.myapplications.mydocscanner.screens.StatusScreen
import com.myapplications.mydocscanner.viewmodel.QrViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Create a single ViewModel instance to be shared across screens
    val qrViewModel: QrViewModel = viewModel()

    NavHost(navController = navController, startDestination = "scanner_screen") {
        composable("scanner_screen") {
            ScannerScreen(
                navController = navController,
                onCodeScanned = { code, status ->
                    qrViewModel.addItem(code, status)
                }
            )
        }
        composable("status_screen") {
            StatusScreen(viewModel = qrViewModel)
        }
    }
}
