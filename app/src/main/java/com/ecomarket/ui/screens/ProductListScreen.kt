package com.ecomarket.ui.screens

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ecomarket.cart.CartViewModel
import com.ecomarket.data.ProductEntity
import com.ecomarket.data.repository.RateRepository
import com.ecomarket.data.sync.ProductSync
import com.ecomarket.data.user.UserRole
import com.ecomarket.di.Graph
import com.ecomarket.di.NetworkModule
import com.ecomarket.navigation.Routes
import com.ecomarket.store.StoreViewModel
import com.ecomarket.util.asCLP
import com.ecomarket.util.finalPrice
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onOpenProduct: (String) -> Unit,
    onOpenCart: () -> Unit,
    navController: NavController,
    storeVm: StoreViewModel,
    cartVm: CartViewModel
) {
    // Productos (como antes)
    val all by storeVm.products.collectAsState(initial = emptyList())
    val categories = remember(all) { listOf("Todos") + all.map { it.category }.distinct() }
    val userRole = Graph.loggedInUser?.role

    var query by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("Todos") }

    val filtered = remember(all, query, selectedCat) {
        all.filter { p ->
            (selectedCat == "Todos" || p.category == selectedCat) &&
                    (query.isBlank() || p.name.contains(query, ignoreCase = true))
        }
    }

    // API externa + sync
    val snackbarHostState = remember { SnackbarHostState() }
    var isSyncing by remember { mutableStateOf(false) }
    val rateRepo = remember { RateRepository(NetworkModule.externalApi) }
    var usdToClp by remember { mutableStateOf<Double?>(null) }
    val scope = rememberCoroutineScope()

    // Obtenemos la tasa REAL desde la API
    LaunchedEffect(Unit) {
        val result = runCatching { rateRepo.usdToClp() }
        result.onSuccess { tasa ->
            Log.d("EcoMarketRate", "Tasa USD->CLP desde API: $tasa")
            usdToClp = tasa
        }.onFailure { e ->
            Log.e("EcoMarketRate", "Error obteniendo tasa USD->CLP", e)
            usdToClp = null   // sin tasa → mostraremos "USD N/A"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (userRole == UserRole.ADMIN) "Panel de Admin" else "EcoMarket")
                        Spacer(Modifier.width(12.dp))
                        usdToClp?.let { rate ->
                            AssistChip(
                                onClick = {},
                                label = { Text("1 USD = ${rate.toInt()} CLP") }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenCart) {
                        Icon(imageVector = Icons.Filled.AddShoppingCart, contentDescription = "Carrito")
                    }
                    IconButton(
                        onClick = { scope.launch { syncNow(snackbarHostState) { isSyncing = it } } }
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualizar catálogo")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (userRole == UserRole.ADMIN) {
                FloatingActionButton(onClick = { navController.navigate(Routes.productEdit("new")) }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Busca productos…") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                categories.forEach { cat ->
                    AssistChip(
                        onClick = { selectedCat = cat },
                        label = { Text(cat) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            if (filtered.isEmpty() && isSyncing) {
                EmptyState(isLoading = true, onRefresh = { /* no-op */ })
            } else if (filtered.isEmpty()) {
                EmptyState(
                    isLoading = false,
                    onRefresh = { scope.launch { syncNow(snackbarHostState) { isSyncing = it } } }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            userRole = userRole,
                            onOpen = { onOpenProduct(product.id) },
                            onAdd = { cartVm.add(product.id) },
                            onEdit = { navController.navigate(Routes.productEdit(product.id)) },
                            onDelete = {
                                storeVm.viewModelScope.launch {
                                    Graph.repository.deleteProduct(product)
                                }
                            },
                            usdToClp = usdToClp
                        )
                    }
                }
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
private fun ProductCard(
    product: ProductEntity,
    userRole: UserRole?,
    onOpen: () -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    usdToClp: Double?
) {
    ElevatedCard(onClick = onOpen) {
        Column(Modifier.padding(10.dp)) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(product.name, style = MaterialTheme.typography.titleSmall, maxLines = 2)

            // Precio CLP + USD
            Column {
                Text(product.finalPrice().asCLP(), style = MaterialTheme.typography.titleMedium)
                usdToClp?.let { rate ->
                    if (rate > 0) {
                        val usd = product.finalPrice() / rate
                        Text(
                            String.format("USD %.2f", usd),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            "USD N/A",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } ?: Text(
                    "USD N/A",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(6.dp))
            if (userRole == UserRole.ADMIN) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Edit, "Editar")
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Delete, "Borrar")
                    }
                }
            } else {
                FilledTonalButton(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.AddShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Agregar")
                }
            }
        }
    }
}

/* ---------- Helpers ---------- */

private suspend fun syncNow(
    snackbarHostState: SnackbarHostState,
    setLoading: (Boolean) -> Unit
) {
    setLoading(true)
    val res = ProductSync.refreshFromServer()
    setLoading(false)
    res.onSuccess { snackbarHostState.showSnackbar("Sincronizados $it productos") }
        .onFailure {
            val msg = it.message ?: "Error de red"
            snackbarHostState.showSnackbar("Error al sincronizar: $msg")
        }
}
