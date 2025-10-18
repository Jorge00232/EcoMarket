package com.ecomarket.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.ecomarket.cart.CartViewModel
import com.ecomarket.data.Categories
import com.ecomarket.data.ProductEntity
import com.ecomarket.data.user.UserRole
import com.ecomarket.di.Graph
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
    navController: androidx.navigation.NavController,
    storeVm: StoreViewModel,
    cartVm: CartViewModel
) {
    val all by storeVm.products.collectAsState()
    val userRole = Graph.loggedInUser?.role

    var query by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("Todos") }

    // Catálogo fijo para los chips
    val catOptions = remember { listOf("Todos") + Categories.all }

    val filtered = all.filter { p ->
        (selectedCat == "Todos" || p.category == selectedCat) &&
                (query.isBlank() || p.name.contains(query, ignoreCase = true))
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (userRole == UserRole.ADMIN) "Panel de Admin" else "EcoMarket") },
                actions = { IconButton(onClick = onOpenCart) { Icon(Icons.Filled.AddShoppingCart, contentDescription = "Carrito") } }
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
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Busca productos…") }, singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp)) {
                catOptions.forEach { cat ->
                    AssistChip(
                        onClick = { selectedCat = cat },
                        label = { Text(cat) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { product ->
                    var showConfirm by remember { mutableStateOf(false) }

                    ProductCard(
                        product = product,
                        userRole = userRole,
                        onOpen = { onOpenProduct(product.id) },
                        onAdd = { cartVm.add(product.id) },
                        onEdit = { navController.navigate(Routes.productEdit(product.id)) },
                        onDelete = { showConfirm = true }
                    )

                    if (showConfirm) {
                        AlertDialog(
                            onDismissRequest = { showConfirm = false },
                            title = { Text("Eliminar producto") },
                            text = { Text("¿Seguro que deseas eliminar \"${product.name}\"?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showConfirm = false
                                    storeVm.viewModelScope.launch {
                                        Graph.repository.deleteProduct(product)
                                        snackbarHostState.showSnackbar("Producto eliminado")
                                    }
                                }) { Text("Eliminar") }
                            },
                            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancelar") } }
                        )
                    }
                }
            }
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
    onDelete: () -> Unit
) {
    ElevatedCard(onClick = onOpen) {
        Column(Modifier.padding(10.dp)) {
            AsyncImage(model = product.imageUrl, contentDescription = product.name,
                modifier = Modifier.fillMaxWidth().height(110.dp))
            Spacer(Modifier.height(8.dp))
            Text(product.name, style = MaterialTheme.typography.titleSmall, maxLines = 2)
            Text(product.finalPrice().asCLP(), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            if (userRole == UserRole.ADMIN) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Edit, "Editar") }
                    IconButton(onClick = onDelete, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Delete, "Borrar") }
                }
            } else {
                FilledTonalButton(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.AddShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(6.dp)); Text("Agregar")
                }
            }
        }
    }
}
