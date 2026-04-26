package de.gosmik.greenlens.ui.screen.main

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gosmik.greenlens.ui.components.OpenCameraFab
import de.gosmik.greenlens.ui.screen.barcode.BarcodeScannerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val showBarcodeScanner by viewModel.showBarcodeScanner.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    if (showBarcodeScanner) {
        BarcodeScannerScreen(
            onDismiss = { viewModel.onBarcodeScannerDismissed() }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Green Lens") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            floatingActionButton = {
                OpenCameraFab(onFabClick = viewModel::onFabClicked)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 4.dp)
            ) {

            }
        }
    }
}
