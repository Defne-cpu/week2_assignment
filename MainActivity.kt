
package com.example.week2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.week2.ui.theme.Week2_assignmentTheme

private val hardwareItems = listOf(
    HardwareItem("101", "MacBook Pro M3 Max", "Geliştirici Bilgisayarı", 3499.0, 5),
    HardwareItem("102", "NVIDIA RTX 4090 Sunucu", "Yapay Zeka / GPU", 1599.0, 2),
    HardwareItem("103", "Google Pixel 8 Pro", "Mobil Test Cihazı", 999.0, 8),
    HardwareItem("104", "Dell UltraSharp 49", "Monitör", 1199.0, 4),
    HardwareItem("105", "Sony WH-1000XM5 Kulaklık", "Odaklanma Ekipmanı", 399.0, 12),
    HardwareItem("106", "Raspberry Pi 5 8GB", "IoT Geliştirme Kiti", 85.0, 25),
    HardwareItem("107", "HHKB Professional Keyboard", "Ergonomik Klavye", 299.0, 0),
    HardwareItem("108", "AMD Ryzen Threadripper", "Yapay Zeka / GPU", 4999.0, 1)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week2_assignmentTheme {
                DevClusterApp()
            }
        }
    }
}

@Composable
fun DevClusterApp() {
    var budget by remember { mutableStateOf(10000.0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("Tümü") }

    val categories = listOf("Tümü") + hardwareItems.map { it.category }.distinct()

    val filteredItems = remember(searchQuery, selectedCategory) {
        hardwareItems.filter {
            (it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)) &&
                    (selectedCategory == "Tümü" || it.category == selectedCategory)
        }
    }

    val totalValue = remember {
        hardwareItems.sumOf { it.cost * it.stockCount }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "DevCluster",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Donanım ve Sunucu Kaynak Yönetimi")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Toplam envanter değeri: $${"%.2f".format(totalValue)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Donanım ara") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Kategori seç")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(selectedCategory)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Bütçe: $${"%.2f".format(budget)}")

            Slider(
                value = budget.toFloat(),
                onValueChange = { budget = it.toDouble() },
                valueRange = 0f..40000f
            )

            Text("Gösterilen ürün: ${filteredItems.size}")

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = filteredItems,
                    key = { it.id }
                ) { item ->
                    HardwareCard(
                        item = item,
                        onClick = {
                            budget = (budget - item.cost).coerceAtLeast(0.0)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HardwareCard(
    item: HardwareItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.name, fontWeight = FontWeight.Bold)
            Text("Kategori: ${item.category}")
            Text("Birim maliyet: $${"%.2f".format(item.cost)}")
            Text("Stok: ${item.stockCount}")
            if (item.stockCount == 0) {
                Text("Stokta yok", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun HardwareSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Donanım ara") },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun DevClusterPreview() {
    Week2_assignmentTheme {
        DevClusterApp()
    }
}
