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
    navController: androidx.navigation.NavController, // Necesario para navegar
    storeVm: StoreViewModel,
    cartVm: CartViewModel
) {
    val all by storeVm.products.collectAsState()
    val categories = listOf("Todos") + all.map { it.category }.distinct()
    val userRole = Graph.loggedInUser?.role // Obtenemos el rol del usuario logueado

    var query by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("Todos") }

    val filtered = all.filter { p ->
        (selectedCat == "Todos" || p.category == selectedCat) &&
                (query.isBlank() || p.name.contains(query, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (userRole == UserRole.ADMIN) "Panel de Admin" else "EcoMarket") },
                actions = {
                    IconButton(onClick = onOpenCart) {
                        Icon(imageVector = Icons.Filled.AddShoppingCart, contentDescription = "Carrito")
                    }
                }
            )
        },
        // AÑADIMOS EL BOTÓN FLOTANTE PARA EL ADMIN
        floatingActionButton = {
            if (userRole == UserRole.ADMIN) {
                FloatingActionButton(onClick = { navController.navigate(Routes.productEdit("new")) }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // ... (El TextField, los Chips de categoría y los Spacers se quedan igual) ...
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Busca productos…") }, singleLine = true)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp)) {
                categories.forEach { cat -> AssistChip(onClick = { selectedCat = cat }, label = { Text(cat) }, modifier = Modifier.padding(end = 8.dp)) }
            }
            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        userRole = userRole, // Pasamos el rol
                        onOpen = { onOpenProduct(product.id) },
                        onAdd = { cartVm.add(product.id) },
                        onEdit = { navController.navigate(Routes.productEdit(product.id)) },
                        onDelete = { storeVm.viewModelScope.launch { Graph.repository.deleteProduct(product) } }
                    )
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
            // ... (La imagen, nombre y precio se quedan igual) ...
            AsyncImage(model = product.imageUrl, contentDescription = product.name, modifier = Modifier.fillMaxWidth().height(110.dp))
            Spacer(Modifier.height(8.dp))
            Text(product.name, style = MaterialTheme.typography.titleSmall, maxLines = 2)
            Text(product.finalPrice().asCLP(), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            // MOSTRAMOS BOTONES DIFERENTES SEGÚN EL ROL
            if (userRole == UserRole.ADMIN) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Edit, "Editar") }
                    IconButton(onClick = onDelete, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Delete, "Borrar") }
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