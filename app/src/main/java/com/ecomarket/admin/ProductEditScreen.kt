package com.ecomarket.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val vm: ProductEditViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProductEditViewModel(productId) as T
            }
        }
    )
    val ui = vm.uiState
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ui.isNewProduct) "Añadir Producto" else "Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { inner ->
        if (ui.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(inner)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Nombre (requerido)
                OutlinedTextField(
                    value = ui.name,
                    onValueChange = vm::onNameChange,
                    label = { Text("Nombre del Producto") },
                    isError = ui.nameError != null,
                    supportingText = { ui.nameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                // Precio (requerido)
                OutlinedTextField(
                    value = ui.price,
                    onValueChange = vm::onPriceChange,
                    label = { Text("Precio") },
                    isError = ui.priceError != null,
                    supportingText = { ui.priceError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                // Categoría (requerido) - Selector con menú desplegable
                ExposedDropdownMenuBox(
                    expanded = categoryMenuExpanded,
                    onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = ui.category,
                        onValueChange = { },
                        label = { Text("Categoría") },
                        isError = ui.categoryError != null,
                        supportingText = { ui.categoryError?.let { Text(it) } },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false }
                    ) {
                        vm.categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    vm.onCategoryChange(option)
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                // URL de Imagen (opcional)
                OutlinedTextField(
                    value = ui.imageUrl,
                    onValueChange = vm::onImageUrlChange,
                    label = { Text("URL de la Imagen (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                // Descripción (opcional)
                OutlinedTextField(
                    value = ui.description,
                    onValueChange = vm::onDescriptionChange,
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
                Spacer(Modifier.height(12.dp))

                // Descuento (opcional 0–100)
                OutlinedTextField(
                    value = ui.discountPercent,
                    onValueChange = vm::onDiscountChange,
                    label = { Text("Porcentaje de Descuento (0–100, opcional)") },
                    isError = ui.discountError != null,
                    supportingText = { ui.discountError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                ui.saveError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                }

                Button(
                    onClick = { vm.saveProduct { navController.popBackStack() } },
                    enabled = !ui.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (ui.isSaving) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    } else {
                        Text("Guardar Producto")
                    }
                }
            }
        }
    }
}
