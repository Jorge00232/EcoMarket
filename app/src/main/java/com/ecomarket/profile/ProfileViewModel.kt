package com.ecomarket.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecomarket.data.user.UserEntity
import com.ecomarket.di.Graph
import kotlinx.coroutines.launch
import android.net.Uri

// Ahora el UiState guardará todos los datos del perfil
data class ProfileUiState(
    val profileImageUri: Uri? = null,
    val email: String = "",
    val name: String = "",
    val birthDate: String = "",
    val shippingAddress: String = "",
    val currentPassword: String = "", // Para la lógica de cambio de contraseña
    val newPassword: String = "",
    val message: String? = null // Para mostrar mensajes como "¡Guardado!"
)

class ProfileViewModel : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    private var currentUser: UserEntity? = null

    init {
        // Cuando el ViewModel se crea, cargamos los datos del usuario logueado
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        currentUser = Graph.loggedInUser
        currentUser?.let { user ->
            uiState = uiState.copy(
                email = user.email,
                name = user.name ?: "",
                birthDate = user.birthDate ?: "",
                shippingAddress = user.shippingAddress ?: ""
            )
        }
    }

    // --- Funciones para manejar los cambios en los campos de texto ---
    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value)
    }

    fun onBirthDateChange(value: String) {
        uiState = uiState.copy(birthDate = value)
    }

    fun onShippingAddressChange(value: String) {
        uiState = uiState.copy(shippingAddress = value)
    }

    fun onNewPasswordChange(value: String) {
        uiState = uiState.copy(newPassword = value)
    }


    // --- Función para guardar los cambios ---
    fun saveProfileChanges() {
        viewModelScope.launch {
            val userToUpdate = currentUser?.copy(
                name = uiState.name,
                birthDate = uiState.birthDate,
                shippingAddress = uiState.shippingAddress,
                // Si el campo de nueva contraseña no está vacío, la actualizamos
                passHash = uiState.newPassword.ifBlank { currentUser!!.passHash }
            )

            if (userToUpdate != null) {
                Graph.repository.updateUser(userToUpdate)
                // Actualizamos el usuario logueado en el Graph también
                Graph.login(userToUpdate)
                uiState = uiState.copy(message = "¡Datos guardados con éxito!")
            }
        }
    }

    fun clearMessage() {
        uiState = uiState.copy(message = null)
    }
    fun onProfileImageChange(uri: Uri?) {
        uiState = uiState.copy(profileImageUri = uri)
    }
}