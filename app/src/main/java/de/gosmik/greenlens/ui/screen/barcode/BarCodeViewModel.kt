package de.gosmik.greenlens.ui.screen.barcode

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gosmik.greenlens.data.openfoodfacts.model.Product
import de.gosmik.greenlens.data.openfoodfacts.repository.ProductRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BarcodeScannerUiState(
    val scannedCode: String? = null,
    val isScanning: Boolean = true,
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class BarcodeScannerViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(BarcodeScannerUiState())
    val uiState: StateFlow<BarcodeScannerUiState> = _uiState.asStateFlow()

    private val _torchEnabled = MutableStateFlow(false)
    val torchEnabled: StateFlow<Boolean> = _torchEnabled.asStateFlow()

    private var activeCamera: androidx.camera.core.Camera? = null

    fun onCameraReady(camera: androidx.camera.core.Camera) {
        activeCamera = camera
        viewModelScope.launch {
            delay(100)
            activeCamera?.cameraControl?.enableTorch(false)
            _torchEnabled.update { false }
        }
    }

    fun onTorchToggled() {
        val newValue = !_torchEnabled.value
        _torchEnabled.update { newValue }
        activeCamera?.cameraControl?.enableTorch(newValue)
    }

    fun onBarcodeDetected(code: String) {
        if (!_uiState.value.isScanning) return
        activeCamera?.cameraControl?.enableTorch(false)
        _torchEnabled.update { false }
        _uiState.update { it.copy(scannedCode = code, isScanning = false, isLoading = true) }
        viewModelScope.launch {
            repository.getProduct(code)
                .onSuccess { product ->
                    _uiState.update { it.copy(product = product, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun onError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    fun resetScanner() {
        activeCamera?.cameraControl?.enableTorch(false)
        _torchEnabled.update { false }
        activeCamera = null
        viewModelScope.launch {
            delay(100)
            _uiState.update { BarcodeScannerUiState() }
        }
    }

    fun onDismiss() {
        activeCamera?.cameraControl?.enableTorch(false)
        _torchEnabled.update { false }
        activeCamera = null
    }
}