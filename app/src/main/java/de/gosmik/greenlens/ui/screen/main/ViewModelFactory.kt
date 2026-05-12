package de.gosmik.greenlens.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.gosmik.greenlens.data.openfoodfacts.api.OpenFoodFactsApi
import de.gosmik.greenlens.data.openfoodfacts.repository.ProductRepository

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = OpenFoodFactsApi()
        val repository = ProductRepository(api)
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(repository) as T
    }
}