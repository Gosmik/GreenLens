package de.gosmik.greenlens.data.openfoodfacts.repository

import android.util.Log
import de.gosmik.greenlens.data.openfoodfacts.api.OpenFoodFactsApi
import de.gosmik.greenlens.data.openfoodfacts.model.Product
import de.gosmik.greenlens.data.openfoodfacts.model.SearchFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ProductRepository(
    private val api: OpenFoodFactsApi = OpenFoodFactsApi()
) {

    private val cache = mutableMapOf<String, List<Product>>()

    suspend fun getProduct(barcode: String): Result<Product> {
        return try {
            val response = api.getProduct(barcode)
            if (response.status == 1 && response.product != null) {
                Result.success(response.product)
            } else {
                Result.failure(Exception("Product not Found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String, filter: SearchFilter = SearchFilter.MOST_SCANNED): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            var lastException: Exception? = null
            repeat(3) { attempt ->
                try {
                    val result = api.searchProducts(query, filter).products
                    return@withContext Result.success(result)
                } catch (e: Exception) {
                    lastException = e
                    delay(1000L * (attempt + 1))
                }
            }
            Result.failure(lastException ?: Exception("Unbekannter Fehler"))
        }
    }

    fun getCachedSuggestions(query: String, filter: SearchFilter): List<Product>? {
        return cache["${query.trim().lowercase()}_${filter.name}"]
    }

    fun cacheSuggestions(query: String, filter: SearchFilter, products: List<Product>) {
        cache["${query.trim().lowercase()}_${filter.name}"] = products
    }
}