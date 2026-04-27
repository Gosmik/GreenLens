package de.gosmik.greenlens.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gosmik.greenlens.ui.data.openfoodfacts.model.Product
import de.gosmik.greenlens.ui.data.openfoodfacts.repository.ProductRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ProductRepository) : ViewModel() {
    // For Toast Events
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _showBarcodeScanner = MutableStateFlow(false)
    val showBarcodeScanner: StateFlow<Boolean> = _showBarcodeScanner.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.length >= 2) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                _isSearching.value = true
                try {
                    _searchResults.value = repository.searchProducts(query)
                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _searchResults.value = emptyList()
                } finally {
                    _isSearching.value = false
                }
            }
        } else {
            searchJob?.cancel()
            _searchResults.value = emptyList()
        }
    }

    fun onSearchCleared() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun onFabClicked() {
        viewModelScope.launch {
            _showBarcodeScanner.update { true }
        }
    }

    fun onBarcodeScannerDismissed() {
        _showBarcodeScanner.update { false }
    }
}