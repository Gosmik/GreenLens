package de.gosmik.greenlens.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import de.gosmik.greenlens.ui.data.openfoodfacts.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSearchBar(
    query: String,
    results: List<Product>,
    isSearching: Boolean,
    onQueryChanged: (String) -> Unit,
    onProductSelected: (Product) -> Unit,
    onCleared: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { newQuery ->
                    onQueryChanged(newQuery)
                    expanded = newQuery.isNotEmpty()
                },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("Search products...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            onCleared()
                            expanded = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        if (isSearching) {
            CircularProgressIndicator(modifier = Modifier
                .padding(16.dp)
            )
        } else {
            results.forEach { product ->
                ListItem(
                    headlineContent = { Text(product.name ?: "Unknown") },
                    supportingContent = { Text(product.brand ?: "") },
                    modifier = Modifier.clickable {
                        onProductSelected(product)
                        expanded = false
                    }
                )
            }
        }
    }
}