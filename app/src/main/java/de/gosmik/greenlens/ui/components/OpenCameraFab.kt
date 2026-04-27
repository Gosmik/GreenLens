package de.gosmik.greenlens.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.outlined.Camera

@Composable
fun OpenCameraFab(
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onFabClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Camera,
            contentDescription = "Open Camera for Bar code"
        )
    }
}
