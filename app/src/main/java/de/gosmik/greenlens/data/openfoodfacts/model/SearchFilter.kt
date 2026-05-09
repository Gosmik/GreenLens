package de.gosmik.greenlens.data.openfoodfacts.model

enum class SearchFilter(val label: String, val apiValue: String) {
    MOST_SCANNED("Most Scanned", "unique_scans_n"),
    BEST_NUTRI_SCORE("Best Nutri-Score", "nutriscore_score"),
    BEST_ECO_SCORE("Best Green-Score", "ecoscore_score"),
    RECENTLY_ADDED("Recently Added", "created_t"),
    RECENTLY_EDITED("Recently Edited", "last_modified_t")
}