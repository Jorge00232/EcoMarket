package com.ecomarket.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ecomarket.data.ProductEntity
import com.ecomarket.data.StoreRepository
import com.ecomarket.di.Graph
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

class StoreViewModel(private val repo: StoreRepository) : ViewModel() {
    val products: StateFlow<List<ProductEntity>> =
        repo.observeProducts()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun product(id: String): StateFlow<ProductEntity?> =
        repo.observeProduct(id)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return StoreViewModel(Graph.repository) as T
            }
        }
    }
}
