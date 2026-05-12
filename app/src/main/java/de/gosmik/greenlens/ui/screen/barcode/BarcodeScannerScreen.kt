package de.gosmik.greenlens.ui.screen.barcode

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import de.gosmik.greenlens.ui.components.CameraPreviewView
import kotlinx.coroutines.launch
import androidx.compose.material3.Icon
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScannerScreen(
    viewModel: BarcodeScannerViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val torchEnabled by viewModel.torchEnabled.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    BackHandler {
        scope.launch {
            viewModel.onDismiss()
            delay(10)
            onDismiss()
        }
    }

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
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    ScanResultScreen(
                        code = uiState.scannedCode!!,
                        onReset = { viewModel.resetScanner() },
                        onDismiss = {
                            viewModel.onDismiss()
                            onDismiss()
                        },
                        product = uiState.product
                    )
                }
            }

            // --- Kamera aktiv ---
            else -> {
                CameraPreviewView(
                    isScanning = uiState.isScanning,
                    onBarcodeDetected = viewModel::onBarcodeDetected,
                    onCameraReady = viewModel::onCameraReady,
                    onError = viewModel::onError,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay: Suchrahmen
                ScannerOverlay(
                    torchEnabled = torchEnabled,
                    onTorchToggled = viewModel::onTorchToggled
                )

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
private fun ScannerOverlay(
    torchEnabled: Boolean,
    onTorchToggled: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {

        IconButton(
            onClick = onTorchToggled,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .padding(top = 60.dp)
        ) {
            Icon(
                imageVector = if (torchEnabled)
                    Icons.Default.FlashOn
                else
                    Icons.Default.FlashOff,
                contentDescription = "Torch",
                tint = if (torchEnabled) Color.Yellow else Color.White
            )
        }
        Box(
            modifier = Modifier
                .width(250.dp)
                .height(125.dp)
                .align(Alignment.Center)
                .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
        )
        Text(
            text = "Move Barcode in Area",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}