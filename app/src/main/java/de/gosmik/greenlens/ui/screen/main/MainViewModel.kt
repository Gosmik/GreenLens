package de.gosmik.greenlens.ui.screen.main

import android.util.Log
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.onSuccess
import kotlin.onFailure

class MainViewModel(private val repository: ProductRepository) : ViewModel() {

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

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _showLicenses = MutableStateFlow(false)
    val showLicenses: StateFlow<Boolean> = _showLicenses.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(600)
                .filter { it.length >= 2 }
                .collectLatest { query ->
                    try {
                        _isSearching.value = true
                        Log.d("apiopenfoodfacts", "Raw Produkte: $query")
                        repository.searchProducts(query)
                            .onSuccess { products ->
                                Log.d("apiopenfoodfacts", "Produkte: $products")
                                _searchResults.value = products
                            }
                            .onFailure { error ->
                                _searchResults.value = emptyList()
                            }
                    } finally {
                        _isSearching.value = false
                    }
                }
        }
    }

    fun onProductSelected(product: Product) {
        _selectedProduct.value = product
    }

    fun onProductDismissed() {
        _selectedProduct.value = null
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) _searchResults.value = emptyList()
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

    fun onOpenLicenses() {
        _showLicenses.update { true }
    }

    fun onLicensesDismissed() {
        _showLicenses.update { false }
    }
}