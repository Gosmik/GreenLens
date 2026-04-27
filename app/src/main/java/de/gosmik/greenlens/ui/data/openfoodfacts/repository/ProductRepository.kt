package de.gosmik.greenlens.ui.data.openfoodfacts.repository

import de.gosmik.greenlens.ui.data.openfoodfacts.api.OpenFoodFactsApi
import de.gosmik.greenlens.ui.data.openfoodfacts.model.Product

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
}