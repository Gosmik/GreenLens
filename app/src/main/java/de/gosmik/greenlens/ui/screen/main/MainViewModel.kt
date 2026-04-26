package de.gosmik.greenlens.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // For Toast Events
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _showBarcodeScanner = MutableStateFlow(false)
    val showBarcodeScanner: StateFlow<Boolean> = _showBarcodeScanner.asStateFlow()

    fun onFabClicked() {
        viewModelScope.launch {
            _showBarcodeScanner.update { true }
        }
    }

    fun onBarcodeScannerDismissed() {
        _showBarcodeScanner.update { false }
    }
}