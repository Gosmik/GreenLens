package de.gosmik.greenlens.ui.screen.barcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gosmik.greenlens.data.openfoodfacts.model.Product
import de.gosmik.greenlens.data.openfoodfacts.repository.ProductRepository
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

    fun onBarcodeDetected(code: String) {
        if (!_uiState.value.isScanning) return

        _uiState.update {
            it.copy(
                scannedCode = code,
                isScanning = false,
                isLoading = true
            )
        }

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
        _uiState.update {
            BarcodeScannerUiState()
        }
    }
}