package de.gosmik.greenlens.data.openfoodfacts.api

import de.gosmik.greenlens.data.openfoodfacts.model.ProductResponse
import de.gosmik.greenlens.data.openfoodfacts.model.SearchFilter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import de.gosmik.greenlens.data.openfoodfacts.model.SearchResponse
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

    suspend fun searchProducts(query: String, filter: SearchFilter, pageSize: Int = 7): SearchResponse {
        val httpResponse = client.get("https://world.openfoodfacts.org/cgi/search.pl") {
            parameter("search_terms", query)
            parameter("search_simple", 1)
            parameter("action", "process")
            parameter("json", 1)
            parameter("page_size", pageSize)
            parameter("sort_by", filter.apiValue)
            parameter("sort_by", "unique_scans_n")
            parameter("nocache", 1)
            parameter("fields", "code,product_name,brands,image_url,nutriments,ingredients_analysis_tags,nutriscore_grade,url")
        }

        if (httpResponse.status.value == 503) {
            throw Exception("Server nicht verfügbar (503)")
        }

        return httpResponse.body()
    }
}