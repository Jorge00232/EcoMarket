package com.ecomarket.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecomarket.data.sync.ProductSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductSyncViewModel : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val count: Int) : UiState
        data class Error(val message: String) : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun refresh() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            ProductSync.refreshFromServer()
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Error de sincronización") }
        }
    }

    fun reset() { _state.value = UiState.Idle }
}

