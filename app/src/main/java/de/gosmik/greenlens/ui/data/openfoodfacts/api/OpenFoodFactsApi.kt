package de.gosmik.greenlens.ui.data.openfoodfacts.api

import de.gosmik.greenlens.ui.data.openfoodfacts.model.ProductResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class OpenFoodFactsApi {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getProduct(barcode: String): ProductResponse {
        return client.get("https://world.openfoodfacts.org/api/v2/product/$barcode.json").body()
    }
}