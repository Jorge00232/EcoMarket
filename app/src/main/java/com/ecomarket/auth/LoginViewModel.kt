package com.ecomarket.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecomarket.di.Graph
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null, // Para errores como "Contraseña incorrecta"
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isValid: Boolean = false
)

class LoginViewModel : ViewModel() {

    var ui by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(value: String) {
        val emailErr = Validation.emailErrorOrNull(value)
        ui = ui.copy(
            email = value,
            emailError = emailErr,
            isValid = emailErr == null && ui.passwordError == null && value.isNotBlank() && ui.password.isNotBlank(),
            generalError = null // Limpia el error general al escribir
        )
    }

    fun onPasswordChange(value: String) {
        val passErr = Validation.passwordErrorOrNull(value)
        ui = ui.copy(
            password = value,
            passwordError = passErr,
            isValid = passErr == null && ui.emailError == null && value.isNotBlank() && ui.email.isNotBlank(),
            generalError = null // Limpia el error general al escribir
        )
    }

    fun togglePasswordVisibility() {
        ui = ui.copy(isPasswordVisible = !ui.isPasswordVisible)
    }

    /**
     * Lógica de login REAL: busca el usuario y valida la contraseña.
     */
    fun submit(onSuccess: () -> Unit) {
        if (!ui.isValid || ui.isLoading) return
        ui = ui.copy(isLoading = true, generalError = null)

        viewModelScope.launch {
            val user = Graph.repository.findUserByEmail(ui.email)

            if (user == null) {
                ui = ui.copy(isLoading = false, generalError = "Usuario no encontrado.")
            } else if (user.passHash != ui.password) { // Comparación directa (insegura, pero funcional para el demo)
                ui = ui.copy(isLoading = false, generalError = "Contraseña incorrecta.")
            } else {
                // ¡Éxito! Guardamos el usuario en la sesión del Graph
                Graph.login(user)
                ui = ui.copy(isLoading = false)
                onSuccess()
            }
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit) {
        // La lógica de invitado puede seguir siendo una simulación simple
        onSuccess()
    }
}