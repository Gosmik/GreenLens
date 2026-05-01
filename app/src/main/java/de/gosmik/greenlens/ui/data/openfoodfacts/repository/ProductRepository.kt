package de.gosmik.greenlens.ui.data.openfoodfacts.repository

import android.util.Log
import de.gosmik.greenlens.ui.data.openfoodfacts.api.OpenFoodFactsApi
import de.gosmik.greenlens.ui.data.openfoodfacts.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ProductRepository(
    private val api: OpenFoodFactsApi = OpenFoodFactsApi()
) {

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

    suspend fun searchProducts(query: String): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            var lastException: Exception? = null
            repeat(3) { attempt ->
                try {
                    val result = api.searchProducts(query).products
                    return@withContext Result.success(result)
                } catch (e: Exception) {
                    lastException = e
                    delay(1000L * (attempt + 1))
                }
            }
            Result.failure(lastException ?: Exception("Unbekannter Fehler"))
        }
    }
}