package de.gosmik.greenlens.data.openfoodfacts.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProductResponse(
    val status: Int,
    val product: Product? = null
)

@Serializable
data class Product(
    @SerialName("product_name") val name: String? = null,
    @SerialName("brands") val brand: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("nutriments") val nutriments: Nutriments? = null,
    @SerialName("ingredients_analysis_tags") val vlabel: List<String> = emptyList()
)

@Serializable
data class Nutriments(
    @SerialName("ingredients_analysis_tags") val vlabel: List<String> = emptyList(),
    @SerialName("energy-kcal_100g") val caloriesPer100g: Double? = null,
    @SerialName("proteins_100g") val proteinPer100g: Double? = null,
    @SerialName("carbohydrates_100g") val carbsPer100g: Double? = null,
    @SerialName("fat_100g") val fatPer100g: Double? = null
)

@Serializable
data class SearchResponse(
    @SerialName("products") val products: List<Product> = emptyList()
)

enum class DietLabel {
    VEGAN,
    VEGETARIAN_MAYBE_VEGAN,
    VEGETARIAN,
    MAYBE_VEGETARIAN,
    UNKNOWN,
    NOT_VEGETARIAN
}

fun List<String>.toDietLabel(): DietLabel {
    val isVegan = any { it == "en:vegan" }
    val isMaybeVegan = any { it == "en:maybe-vegan" }

    val isVegetarian = any { it == "en:vegetarian" }
    val isMaybeVegetarian = any { it == "en:maybe-vegetarian" }
    val isNonVegetarian = any { it == "en:non-vegetarian" }

    return when {
        isVegan -> DietLabel.VEGAN
        isVegetarian && isMaybeVegan -> DietLabel.VEGETARIAN_MAYBE_VEGAN
        isVegetarian -> DietLabel.VEGETARIAN
        isMaybeVegetarian -> DietLabel.MAYBE_VEGETARIAN
        isNonVegetarian -> DietLabel.NOT_VEGETARIAN
        else -> DietLabel.UNKNOWN
    }
}