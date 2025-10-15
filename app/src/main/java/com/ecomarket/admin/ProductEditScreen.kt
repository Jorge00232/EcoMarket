package com.ecomarket.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    productId: String,
    navController: androidx.navigation.NavController
) {
    // Creamos el ViewModel pasándole el ID del producto
    val vm: ProductEditViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProductEditViewModel(productId) as T
            }
        }
    )
    val uiState = vm.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isNewProduct) "Añadir Producto" else "Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(value = uiState.name, onValueChange = vm::onNameChange, label = { Text("Nombre del Producto") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = uiState.price, onValueChange = vm::onPriceChange, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = uiState.category, onValueChange = vm::onCategoryChange, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = uiState.imageUrl, onValueChange = vm::onImageUrlChange, label = { Text("URL de la Imagen") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = uiState.description, onValueChange = vm::onDescriptionChange, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth().height(120.dp))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = uiState.discountPercent, onValueChange = vm::onDiscountChange, label = { Text("Porcentaje de Descuento (ej: 10)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        vm.saveProduct { navController.popBackStack() }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Producto")
                }
            }
        }
    }
}