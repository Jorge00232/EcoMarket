package com.ecomarket.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecomarket.data.ProductEntity
import com.ecomarket.di.Graph
import kotlinx.coroutines.launch
import java.util.UUID

// Estado que representa los campos del formulario
data class ProductEditUiState(
    val name: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val description: String = "",
    val discountPercent: String = "",
    val isNewProduct: Boolean = true,
    val isLoading: Boolean = true
)

class ProductEditViewModel(private val productId: String) : ViewModel() {
    var uiState by mutableStateOf(ProductEditUiState())
        private set

    init {
        if (productId == "new") {
            // Es un producto nuevo, dejamos los campos en blanco
            uiState = uiState.copy(isNewProduct = true, isLoading = false)
        } else {
            // Es un producto existente, cargamos sus datos
            uiState = uiState.copy(isNewProduct = false, isLoading = true)
            viewModelScope.launch {
                val product = Graph.repository.getProductById(productId)
                product?.let {
                    uiState = uiState.copy(
                        name = it.name,
                        price = it.price.toString(),
                        imageUrl = it.imageUrl,
                        category = it.category,
                        description = it.description,
                        discountPercent = it.discountPercent?.toString() ?: "",
                        isLoading = false
                    )
                }
            }
        }
    }

    // --- Funciones para actualizar el estado con cada cambio de texto ---
    fun onNameChange(value: String) { uiState = uiState.copy(name = value) }
    fun onPriceChange(value: String) { uiState = uiState.copy(price = value) }
    fun onImageUrlChange(value: String) { uiState = uiState.copy(imageUrl = value) }
    fun onCategoryChange(value: String) { uiState = uiState.copy(category = value) }
    fun onDescriptionChange(value: String) { uiState = uiState.copy(description = value) }
    fun onDiscountChange(value: String) { uiState = uiState.copy(discountPercent = value) }

    // --- Lógica para guardar el producto ---
    fun saveProduct(onSaveFinished: () -> Unit) {
        viewModelScope.launch {
            val product = ProductEntity(
                id = if (uiState.isNewProduct) UUID.randomUUID().toString() else productId,
                name = uiState.name,
                price = uiState.price.toDoubleOrNull() ?: 0.0,
                imageUrl = uiState.imageUrl,
                category = uiState.category,
                description = uiState.description,
                discountPercent = uiState.discountPercent.toIntOrNull()
            )

            if (uiState.isNewProduct) {
                Graph.repository.addProduct(product)
            } else {
                Graph.repository.updateProduct(product)
            }
            onSaveFinished() // Llama a esta función para navegar hacia atrás
        }
    }
}