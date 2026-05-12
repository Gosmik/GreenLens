package de.gosmik.greenlens.ui.screen.barcode

import android.content.ClipData
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import de.gosmik.greenlens.data.openfoodfacts.model.DietLabel
import de.gosmik.greenlens.data.openfoodfacts.model.Nutriments
import de.gosmik.greenlens.data.openfoodfacts.model.Product
import de.gosmik.greenlens.data.openfoodfacts.model.toDietLabel
import kotlinx.coroutines.launch

@Composable
fun PermissionDeniedContent(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (shouldShowRationale)
                "This App needs Camera Access to Scan the Barcode."
            else
                "Please Grant Camera Access for this App in the Settings.",
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        if (shouldShowRationale) {
            Button(onClick = onRequestPermission) {
                Text("Grant Access")
            }
        }
    }
}

@Composable
fun ScanResultScreen(
    code: String,
    product: Product?,
    onReset: () -> Unit,
    onDismiss: () -> Unit = onReset
) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler { onDismiss() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (product != null) {
            Text(
                product.name ?: "-",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(product.brand ?: "", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))

            product.nutriments?.let { n ->
                NutrimentsCard(
                    nutriments = n,
                    product = product
                )
            }
        } else {
            Text("No Product Found", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(8.dp))
        Text(code, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onReset) { Text("Rescan") }
            OutlinedButton(onClick = {
                scope.launch {
                    clipboardManager.setClipEntry(
                        ClipEntry(ClipData.newPlainText("barcode", code))
                    )
                }
            }) { Text("Copy") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val productUrl = if (code.isNotBlank()) {
                "https://world.openfoodfacts.org/product/$code"
            } else {
                product?.url
            }
            productUrl?.let { url ->
                OutlinedButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                }) {
                    Text("Open URL")
                }
            }
        }
    }
}

@Composable
fun NutrimentsCard(
    nutriments: Nutriments,
    product: Product?
) {
    fun Double.round2(): String = "%.2f".format(this)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nutritional values per 100g", style = MaterialTheme.typography.titleSmall)

            val vLabelTag = product?.vlabel?.toDietLabel()

            when (vLabelTag) {
                DietLabel.VEGAN -> {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "🌱 Vegan",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(Modifier.height(8.dp))
                }
                DietLabel.VEGETARIAN_MAYBE_VEGAN -> {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "🌱 Vegetarian (maybe Vegan)",
                        color = Color(0xFF8BC34A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(Modifier.height(8.dp))
                }
                DietLabel.VEGETARIAN -> {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "🥚 Vegetarian",
                        color = Color(0xFF8BC34A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(Modifier.height(8.dp))
                }
                DietLabel.MAYBE_VEGETARIAN -> {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "🥚 Maybe Vegetarian",
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp)
                    Spacer(Modifier.height(8.dp))
                }
                DietLabel.NOT_VEGETARIAN -> {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "❌ Not Vegetarian",
                        color = Color(0xFFF44336),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp)
                    Spacer(Modifier.height(8.dp))
                }
                DietLabel.UNKNOWN -> {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "❓ Unknown",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(Modifier.height(8.dp))
                }
                else -> {}
            }

            nutriments.caloriesPer100g?.let { Text("Calories: ${it.toInt()} kcal") }
            nutriments.proteinPer100g?.let { Text("Protein: ${it.round2()}g") }
            nutriments.carbsPer100g?.let { Text("Carbohydrates: ${it.round2()}g") }
            nutriments.fatPer100g?.let { Text("Fat: ${it.round2()}g") }

            product?.nutriScore
                ?.takeIf { it.isNotBlank() && it != "not-applicable" && it != "unknown" }
                ?.let { score ->
                    Spacer(Modifier.height(16.dp))
                    NutriScoreBar(score = score.uppercase())
                }
        }
    }
}

@Composable
fun NutriScoreBar(score: String) {
    val grades = listOf("A", "B", "C", "D", "E")
    val colors = mapOf(
        "A" to Color(0xFF2d7e43),
        "B" to Color(0xFF95bb3a),
        "C" to Color(0xFFf0ca0f),
        "D" to Color(0xFFd27b19),
        "E" to Color(0xFFc53318)
    )

    Text("Nutri-Score", style = MaterialTheme.typography.labelMedium)
    Spacer(Modifier.height(8.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        grades.forEach { grade ->
            val isActive = grade == score
            val color = colors[grade] ?: Color.Gray

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(if (isActive) 40.dp else 32.dp)
                    .background(
                        color = if (isActive) color else color.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(6.dp)
                    )
            ) {
                Text(
                    text = grade,
                    color = if (isActive) Color.White else Color.White.copy(alpha = 0.6f),
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isActive) 16.sp else 13.sp
                )
            }
        }
    }
}