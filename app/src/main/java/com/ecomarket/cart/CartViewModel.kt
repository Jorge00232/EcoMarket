package com.ecomarket.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ecomarket.data.CartLine
import com.ecomarket.data.StoreRepository
import com.ecomarket.di.Graph
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(private val repo: StoreRepository) : ViewModel() {

    val cart: StateFlow<List<CartLine>> =
        repo.observeCart().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val subtotal: StateFlow<Double> =
        repo.observeSubtotal().stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun add(productId: String) = viewModelScope.launch { repo.addToCart(productId) }
    fun increase(productId: String) = viewModelScope.launch { repo.increase(productId) }
    fun decrease(productId: String) = viewModelScope.launch { repo.decrease(productId) }
    fun remove(productId: String) = viewModelScope.launch { repo.remove(productId) }
    fun clear() = viewModelScope.launch { repo.clearCart() }

    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CartViewModel(Graph.repository) as T
            }
        }
    }
}
