package de.gosmik.greenlens.ui.data.openfoodfacts.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProductResponse(
    val status: Int,
    val product: Product? = null
)

@Serializable
data class Product(
    @SerialName("product_name_en") val name: String? = null,
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

enum class DietLabel {
    VEGAN,
    VEGETARIAN,
    NONE;
}

fun List<String>.toDietLabel(): DietLabel {
    return when {
        any { it.contains("non-vegetarian") } -> DietLabel.NONE
        any { it.contains("non-vegan") } -> DietLabel.VEGETARIAN
        any { it.contains("vegan") } -> DietLabel.VEGAN
        any { it.contains("vegetarian") } -> DietLabel.VEGETARIAN
        else -> DietLabel.NONE
    }
}