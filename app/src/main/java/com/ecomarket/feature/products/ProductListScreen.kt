package com.ecomarket.feature.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ecomarket.data.ProductEntity

// IMPORTANTE para usar `by` con State<T>
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onOpenDetail: (String) -> Unit,
    listVm: ProductListViewModel = viewModel(),
    syncVm: ProductSyncViewModel = viewModel()
) {
    // da valor inicial para que el tipo sea inferible
    val products by listVm.products.collectAsState(initial = emptyList())
    val syncState by syncVm.state.collectAsState(initial = ProductSyncViewModel.UiState.Idle)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(syncState) {
        when (val s = syncState) {
            is ProductSyncViewModel.UiState.Success -> {
                snackbarHostState.showSnackbar("Sincronizados ${s.count} productos")
                syncVm.reset()
            }
            is ProductSyncViewModel.UiState.Error -> {
                snackbarHostState.showSnackbar("Error: ${s.message}")
                syncVm.reset()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(
                        onClick = { syncVm.refresh() },
                        enabled = syncState !is ProductSyncViewModel.UiState.Loading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (products.isEmpty()) {
                EmptyState(
                    isLoading = syncState is ProductSyncViewModel.UiState.Loading,
                    onRefresh = { syncVm.refresh() }
                )
            } else {
                ProductGrid(products = products, onOpenDetail = onOpenDetail)
            }
        }
    }
}

@Composable
private fun EmptyState(isLoading: Boolean, onRefresh: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Sincronizando…")
        } else {
            Text("No hay productos aún")
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRefresh) { Text("Actualizar catálogo") }
        }
    }
}

@Composable
private fun ProductGrid(products: List<ProductEntity>, onOpenDetail: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(products, key = { it.id }) { item ->
            ProductCard(item) { onOpenDetail(item.id) }
        }
    }
}

@Composable
private fun ProductCard(item: ProductEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier.fillMaxWidth().height(140.dp)
            )
            Column(Modifier.padding(12.dp)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(item.category, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                val discount = item.discountPercent
                val priceText = if (discount != null) {
                    val finalPrice = item.price * (1 - discount / 100.0)
                    "$${finalPrice.toInt()}  (-$discount%)"
                } else {
                    "$${item.price.toInt()}"
                }
                Text(priceText, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
