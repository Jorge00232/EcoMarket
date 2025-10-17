package com.ecomarket.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ecomarket.cart.CartViewModel
import com.ecomarket.util.asCLP
import com.ecomarket.util.finalPrice
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartVm: CartViewModel,
    onCheckout: () -> Unit
) {
    val lines = cartVm.cart.collectAsState()
    val subtotal = cartVm.subtotal.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tu carrito de compras") }) },
        bottomBar = {
            Column(Modifier.padding(16.dp)) {
                Text("Subtotal: ${subtotal.value.asCLP()}", style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = onCheckout,
                    enabled = lines.value.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) { Text("Pagar (demo)") }
            }
        }
    ) { inner ->
        if (lines.value.isEmpty()) {
        }
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(lines.value, key = { it.product.id }) { item ->
                AnimatedVisibility(
                    visible = true,
                    exit = shrinkVertically() + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .animateItem()
                ) {
                    ElevatedCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(item.product.name, style = MaterialTheme.typography.titleSmall)
                                Text("Precio: ${item.product.finalPrice().asCLP()}")
                                Text("Cantidad: ${item.qty}")
                            }
                            Row {
                                OutlinedButton(onClick = { cartVm.decrease(item.product.id) }) { Text("-") }
                                Spacer(Modifier.width(6.dp))
                                OutlinedButton(onClick = { cartVm.increase(item.product.id) }) { Text("+") }
                                Spacer(Modifier.width(6.dp))
                                IconButton(onClick = { cartVm.remove(item.product.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Quitar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}