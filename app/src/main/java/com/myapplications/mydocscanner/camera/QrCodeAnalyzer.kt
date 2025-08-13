package com.myapplications.mydocscanner.camera

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image
        if (image != null) {
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)

            // Configure the barcode scanner
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)

            // Process the image
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    // Handle successful scan
                    if (barcodes.isNotEmpty()) {
                        barcodes.firstOrNull()?.rawValue?.let { qrCodeValue ->
                            onQrCodeScanned(qrCodeValue)
                        }
                    }
                }
                .addOnFailureListener {
                    // Handle failure (optional)
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    // Close the image proxy to allow the next frame to be processed
                    imageProxy.close()
                }
        }
    }
}
