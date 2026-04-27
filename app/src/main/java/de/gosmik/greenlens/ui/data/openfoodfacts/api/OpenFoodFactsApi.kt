package de.gosmik.greenlens.ui.data.openfoodfacts.api

import de.gosmik.greenlens.ui.data.openfoodfacts.model.ProductResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import de.gosmik.greenlens.ui.data.openfoodfacts.model.SearchResponse
import io.ktor.client.request.parameter

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

    suspend fun searchProducts(query: String, pageSize: Int = 10): SearchResponse {
        return client.get("https://de.openfoodfacts.org/cgi/search.pl") {
            parameter("search_terms", query)
            parameter("action", "process")
            parameter("json", 1)
            parameter("page_size", pageSize)
        }.body()
    }
}