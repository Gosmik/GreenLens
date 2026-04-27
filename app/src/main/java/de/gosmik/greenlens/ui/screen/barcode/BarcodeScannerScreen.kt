package de.gosmik.greenlens.ui.screen.barcode

import android.Manifest
import android.content.ClipData
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import de.gosmik.greenlens.ui.components.CameraPreviewView
import de.gosmik.greenlens.ui.data.openfoodfacts.model.DietLabel
import de.gosmik.greenlens.ui.data.openfoodfacts.model.Nutriments
import de.gosmik.greenlens.ui.data.openfoodfacts.model.Product
import de.gosmik.greenlens.ui.data.openfoodfacts.model.toDietLabel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScannerScreen(
    viewModel: BarcodeScannerViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    BackHandler {
        onDismiss()
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
                    ScanResultContent(
                        code = uiState.scannedCode!!,
                        onReset = { viewModel.resetScanner() },
                        product = uiState.product
                    )
                }
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
                "This App needs Camera Access to Scan the Barcode."
            else
                "Please Grant Camera Access for this App in the Settings.",
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        if (shouldShowRationale) {
            Button(onClick = onRequestPermission) {
                Text("Grant Access")
            }
        }
    }
}

@Composable
private fun ScanResultContent(
    code: String,
    product: Product?,
    onReset: () -> Unit
) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (product != null) {
            Text(product.name ?: "Unknown Product", style = MaterialTheme.typography.headlineSmall)
            Text(product.brand ?: "", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))

            product.nutriments?.let { n ->
                NutrimentsCard(
                    nutriments = n,
                    product = product
                )
            }
        } else {
            Text("No Product Found", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(8.dp))
        Text(code, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onReset) { Text("Rescan") }
            OutlinedButton(onClick = {
                scope.launch {
                    clipboardManager.setClipEntry(
                        ClipEntry(ClipData.newPlainText("barcode", code))
                    )
                }
            }) { Text("Copy") }
        }
    }
}

@Composable
private fun NutrimentsCard(
    nutriments: Nutriments,
    product: Product?
) {
    fun Double.round2(): String = "%.2f".format(this)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nutritional values per 100g", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            val vLabelTag = product?.vlabel?.toDietLabel()

            when (vLabelTag) {
                DietLabel.VEGAN -> Text("🌱 Vegan", color = Color(0xFF4CAF50))
                DietLabel.VEGETARIAN -> Text("🥚 Vegetarian", color = Color(0xFF8BC34A))
                DietLabel.NONE -> {}
                else -> {}
            }

            Spacer(Modifier.height(8.dp))
            nutriments.caloriesPer100g?.let { Text("Calories: ${it.toInt()} kcal") }
            nutriments.proteinPer100g?.let { Text("Protein: ${it.round2()}g") }
            nutriments.carbsPer100g?.let { Text("Carbohydrates: ${it.round2()}g") }
            nutriments.fatPer100g?.let { Text("Fat: ${it.round2()}g") }
        }
    }
}