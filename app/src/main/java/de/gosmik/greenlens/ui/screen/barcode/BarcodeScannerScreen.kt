package de.gosmik.greenlens.ui.screen.barcode

import android.Manifest
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import de.gosmik.greenlens.ui.components.CameraPreviewView

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScannerScreen(
    viewModel: BarcodeScannerViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // --- Kein Permission ---
            !cameraPermission.status.isGranted -> {
                PermissionDeniedContent(
                    shouldShowRationale = cameraPermission.status.shouldShowRationale,
                    onRequestPermission = { cameraPermission.launchPermissionRequest() }
                )
            }

            // --- Code wurde gescannt ---
            uiState.scannedCode != null -> {
                ScanResultContent(
                    code = uiState.scannedCode!!,
                    onReset = { viewModel.resetScanner() }
                )
            }

            // --- Kamera aktiv ---
            else -> {
                CameraPreviewView(
                    isScanning = uiState.isScanning,
                    onBarcodeDetected = viewModel::onBarcodeDetected,
                    onError = viewModel::onError,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay: Suchrahmen
                ScannerOverlay()

                // Fehler anzeigen
                uiState.error?.let { error ->
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) { Text(error) }
                }
            }
        }
    }
}

// --- Hilfs-Composables ---

@Composable
private fun ScannerOverlay() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                //.size(250.dp)
                .width(250.dp)
                .height(150.dp)
                .align(Alignment.Center)
                .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
        )
        Text(
            text = "Barcode in den Rahmen halten",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

@Composable
private fun ScanResultContent(code: String, onReset: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Gescannter Code", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            text = code,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onReset) {
            Text("Erneut scannen")
        }
    }
}

@Composable
private fun PermissionDeniedContent(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (shouldShowRationale)
                "Die App benötigt Kamera-Zugriff um Barcodes zu scannen."
            else
                "Bitte erteile die Kamera-Berechtigung in den Einstellungen.",
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        if (shouldShowRationale) {
            Button(onClick = onRequestPermission) {
                Text("Berechtigung erteilen")
            }
        }
    }
}