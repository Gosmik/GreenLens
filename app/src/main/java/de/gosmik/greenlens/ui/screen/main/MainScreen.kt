package de.gosmik.greenlens.ui.screen.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gosmik.greenlens.ui.components.OpenCameraFab
import de.gosmik.greenlens.ui.components.ProductSearchBar
import de.gosmik.greenlens.ui.screen.barcode.BarcodeScannerScreen
import de.gosmik.greenlens.ui.screen.barcode.ScanResultContent
import de.gosmik.greenlens.ui.screen.license.LicensesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel(factory = MainViewModelFactory())) {
    val showBarcodeScanner by viewModel.showBarcodeScanner.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val selectedProduct by viewModel.selectedProduct.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    val showLicenses by viewModel.showLicenses.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }

    val searchError by viewModel.searchError.collectAsState()

    val selectedFilter by viewModel.selectedFilter.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    if (showLicenses) {
        LicensesScreen(onDismiss = { viewModel.onLicensesDismissed() })
    } else if (showBarcodeScanner) {
        BarcodeScannerScreen(
            onDismiss = { viewModel.onBarcodeScannerDismissed() }
        )
    } else if (selectedProduct != null) {
        ScanResultContent(
            code = "",
            product = selectedProduct,
            onReset = { viewModel.onProductDismissed() }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Green Lens") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menü"
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Licenses") },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.onOpenLicenses()
                                }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                OpenCameraFab(onFabClick = viewModel::onFabClicked)
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProductSearchBar(
                        query = searchQuery,
                        results = searchResults,
                        isSearching = isSearching,
                        selectedFilter = selectedFilter,
                        onFilterSelected = viewModel::onFilterSelected,
                        onQueryChanged = viewModel::onSearchQueryChanged,
                        onProductSelected = { product ->
                            viewModel.onProductSelected(product)
                        },
                        onCleared = viewModel::onSearchCleared
                    )
                }

                searchError?.let {
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) { Text("Search failed") }
                }
            }
        }
    }
}
