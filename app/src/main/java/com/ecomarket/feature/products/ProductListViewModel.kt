package com.ecomarket.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecomarket.data.ProductEntity
import com.ecomarket.di.Graph
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProductListViewModel : ViewModel() {
    val products: StateFlow<List<ProductEntity>> =
        Graph.database.productDao()
            .getAll()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
