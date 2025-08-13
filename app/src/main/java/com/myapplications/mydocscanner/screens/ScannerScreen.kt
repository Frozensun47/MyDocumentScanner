package com.myapplications.mydocscanner.screens

import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.myapplications.mydocscanner.camera.QrCodeAnalyzer
import com.myapplications.mydocscanner.viewmodel.Status

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    navController: NavController,
    onCodeScanned: (String, Status) -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var scannedCode by remember { mutableStateOf("") }
    var hasScanned by remember { mutableStateOf(false) }

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(key1 = true) {
        cameraPermissionState.launchPermissionRequest()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (cameraPermissionState.status.isGranted) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(ContextCompat.getMainExecutor(ctx), QrCodeAnalyzer { qrCode ->
                                        if (!hasScanned) {
                                            scannedCode = qrCode
                                            hasScanned = true // Stop further scanning until reset
                                        }
                                    })
                                }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalyzer
                                )
                            } catch (e: Exception) {
                                Log.e("ScannerScreen", "Camera binding failed", e)
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Camera permission is required to use the scanner.")
            }
        }

        // Bottom section with TextField and Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = scannedCode,
                onValueChange = { scannedCode = it },
                label = { Text("Scanned Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (scannedCode.isNotBlank()) {
                            onCodeScanned(scannedCode, Status.IN)
                            navController.navigate("status_screen")
                        }
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text("IN")
                }

                Button(
                    onClick = {
                        if (scannedCode.isNotBlank()) {
                            onCodeScanned(scannedCode, Status.OUT)
                            navController.navigate("status_screen")
                        }
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text("OUT")
                }
            }
        }
    }
}
