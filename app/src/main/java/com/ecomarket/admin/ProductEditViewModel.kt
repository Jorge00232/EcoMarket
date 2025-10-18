package com.ecomarket.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecomarket.data.Categories
import com.ecomarket.data.ProductEntity
import com.ecomarket.di.Graph
import kotlinx.coroutines.launch
import java.util.UUID

data class ProductEditUiState(
    val name: String = "",
    val price: String = "",
    val imageUrl: String = "",          // OPCIONAL
    val category: String = "",
    val description: String = "",       // OPCIONAL
    val discountPercent: String = "",   // OPCIONAL (0–100)
    val isNewProduct: Boolean = true,
    val isLoading: Boolean = true,

    // errores por campo
    val nameError: String? = null,
    val priceError: String? = null,
    val categoryError: String? = null,
    val discountError: String? = null,

    // feedback general
    val isSaving: Boolean = false,
    val saveError: String? = null
)

class ProductEditViewModel(private val productId: String) : ViewModel() {

    // catálogo fijo para la UI
    val categories: List<String> = Categories.all

    var uiState by mutableStateOf(ProductEditUiState())
        private set

    init {
        if (productId == "new") {
            uiState = uiState.copy(isNewProduct = true, isLoading = false)
        } else {
            uiState = uiState.copy(isNewProduct = false, isLoading = true)
            viewModelScope.launch {
                val p = Graph.repository.getProductById(productId)
                p?.let {
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

    // --- Helpers de sanitización/normalización ---
    private fun sanitizeDecimalInput(text: String): String {
        var dotSeen = false
        val out = StringBuilder()
        text.forEach { ch ->
            when {
                ch.isDigit() -> out.append(ch)
                ch == '.' || ch == ',' -> if (!dotSeen) { out.append('.'); dotSeen = true }
            }
        }
        return out.toString()
    }
    private fun digitsOnly(text: String): String = text.filter { it.isDigit() }
    private fun priceToDouble(txt: String): Double? =
        txt.ifBlank { return null }.toDoubleOrNull()

    // --- Setters con limpieza y reseteo de error ---
    fun onNameChange(v: String) { uiState = uiState.copy(name = v, nameError = null) }
    fun onPriceChange(v: String) { uiState = uiState.copy(price = sanitizeDecimalInput(v), priceError = null) }
    fun onImageUrlChange(v: String) { uiState = uiState.copy(imageUrl = v) } // opcional
    fun onCategoryChange(v: String) { uiState = uiState.copy(category = v, categoryError = null) }
    fun onDescriptionChange(v: String) { uiState = uiState.copy(description = v) } // opcional
    fun onDiscountChange(v: String) { uiState = uiState.copy(discountPercent = digitsOnly(v), discountError = null) }

    // --- Validaciones ---
    private fun validate(): Boolean {
        val priceVal = priceToDouble(uiState.price)
        val discountVal = uiState.discountPercent.toIntOrNull()

        var ok = true
        var nameErr: String? = null
        var priceErr: String? = null
        var catErr: String? = null
        var discErr: String? = null

        if (uiState.name.isBlank()) nameErr = "Campo requerido".also { ok = false }

        // categoría debe estar en el catálogo
        if (uiState.category.isBlank()) {
            catErr = "Selecciona una categoría"; ok = false
        } else if (uiState.category !in categories) {
            catErr = "Categoría inválida"; ok = false
        }

        if (priceVal == null || priceVal < 0.0) {
            priceErr = "Ingresa un número válido"; ok = false
        }

        if (uiState.discountPercent.isNotBlank()) {
            if (discountVal == null || discountVal !in 0..100) {
                discErr = "Debe estar entre 0 y 100"; ok = false
            }
        }

        uiState = uiState.copy(
            nameError = nameErr,
            priceError = priceErr,
            categoryError = catErr,
            discountError = discErr
        )
        return ok
    }

    fun saveProduct(onSaveFinished: () -> Unit) {
        if (!validate() || uiState.isSaving) return

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isSaving = true, saveError = null)
                val product = ProductEntity(
                    id = if (uiState.isNewProduct) UUID.randomUUID().toString() else productId,
                    name = uiState.name.trim(),
                    price = priceToDouble(uiState.price) ?: 0.0,
                    imageUrl = uiState.imageUrl.trim(),
                    category = uiState.category.trim(),
                    description = uiState.description.trim(),
                    discountPercent = uiState.discountPercent.toIntOrNull()
                )
                if (uiState.isNewProduct) Graph.repository.addProduct(product)
                else Graph.repository.updateProduct(product)
                onSaveFinished()
            } catch (e: Exception) {
                uiState = uiState.copy(saveError = "No se pudo guardar. Intenta nuevamente.")
            } finally {
                uiState = uiState.copy(isSaving = false)
            }
        }
    }
}
