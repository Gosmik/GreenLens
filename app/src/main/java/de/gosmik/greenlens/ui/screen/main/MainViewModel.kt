package de.gosmik.greenlens.ui.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gosmik.greenlens.data.openfoodfacts.model.Product
import de.gosmik.greenlens.data.openfoodfacts.model.SearchFilter
import de.gosmik.greenlens.data.openfoodfacts.repository.ProductRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
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

    private val _searchError = MutableStateFlow<Throwable?>(null)
    val searchError: StateFlow<Throwable?> = _searchError.asStateFlow()

    private val _selectedFilter = MutableStateFlow(SearchFilter.MOST_SCANNED)
    val selectedFilter: StateFlow<SearchFilter> = _selectedFilter.asStateFlow()

    init {
        viewModelScope.launch {
            combine(_searchQuery, _selectedFilter) { query, filter -> query to filter }
                .map { (query, filter) -> query.trim() to filter }
                .debounce(1000)
                .distinctUntilChanged()
                .filter { (query, _) -> query.length >= 2 }
                .collectLatest { (query, filter) ->
                    val cached = repository.getCachedSuggestions(query, filter)
                    if (cached != null) {
                        _searchResults.value = cached
                        return@collectLatest
                    }
                    _isSearching.value = true
                    try {
                        val products = repository.searchProducts(query, filter)
                            .getOrElse { error ->
                                _searchError.value = error
                                null
                            }
                        if (products != null) {
                            _searchResults.value = products
                            repository.cacheSuggestions(query, filter, products)
                        }
                    } finally {
                        _isSearching.value = false
                    }
                }
        }
    }

    fun onFilterSelected(filter: SearchFilter) {
        _selectedFilter.value = filter
        _searchResults.value = emptyList()
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