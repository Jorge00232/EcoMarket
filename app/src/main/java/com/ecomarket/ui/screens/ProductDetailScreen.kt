package com.ecomarket.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ecomarket.cart.CartViewModel
import com.ecomarket.store.StoreViewModel
import com.ecomarket.util.asCLP
import com.ecomarket.util.finalPrice
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
    onGoToCart: () -> Unit,
    storeVm: StoreViewModel,
    cartVm: CartViewModel
) {
    val product by storeVm.product(productId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onGoToCart) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                    }
                }
            )
        }
    ) { inner ->
        val p = product
        if (p == null) {
            Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) { Text("Producto no encontrado") }
            return@Scaffold
        }

        var qty by remember { mutableStateOf(1) }

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = p.imageUrl,
                contentDescription = p.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Text(p.name, style = MaterialTheme.typography.headlineSmall)
            Text(p.description, style = MaterialTheme.typography.bodyMedium)
            Text("Precio: ${p.finalPrice().asCLP()}", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { if (qty > 1) qty-- }) { Text("-") }
                Text(
                    "Cantidad: $qty",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 10.dp)
                )
                OutlinedButton(onClick = { qty++ }) { Text("+") }
            }

            Button(
                onClick = {
                    repeat(qty) { cartVm.add(p.id) }
                    onGoToCart()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar al carrito") }
        }
    }
}
