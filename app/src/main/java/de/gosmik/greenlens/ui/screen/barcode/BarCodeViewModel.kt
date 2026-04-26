package de.gosmik.greenlens.ui.screen.barcode

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BarcodeScannerUiState(
    val scannedCode: String? = null,
    val isScanning: Boolean = true,
    val error: String? = null
)

class BarcodeScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BarcodeScannerUiState())
    val uiState: StateFlow<BarcodeScannerUiState> = _uiState.asStateFlow()

    fun onBarcodeDetected(code: String) {
        if (!_uiState.value.isScanning) return

        _uiState.update {
            it.copy(
                scannedCode = code,
                isScanning = false
            )
        }
    }

    fun onError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    fun resetScanner() {
        _uiState.update {
            BarcodeScannerUiState()
        }
    }
}