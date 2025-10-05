package com.ecomarket.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
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
            isValid = emailErr == null && ui.passwordError == null && value.isNotBlank() && ui.password.isNotBlank()
        )
    }

    fun onPasswordChange(value: String) {
        val passErr = Validation.passwordErrorOrNull(value)
        ui = ui.copy(
            password = value,
            passwordError = passErr,
            isValid = passErr == null && ui.emailError == null && value.isNotBlank() && ui.email.isNotBlank()
        )
    }

    fun togglePasswordVisibility() {
        ui = ui.copy(isPasswordVisible = !ui.isPasswordVisible)
    }

    /**
     * Simulación de login: si las validaciones están OK, “éxito” tras un pequeño delay.
     */
    fun submit(onSuccess: () -> Unit) {
        if (!ui.isValid || ui.isLoading) return
        ui = ui.copy(isLoading = true)

        viewModelScope.launch {
            delay(900) // simular red
            ui = ui.copy(isLoading = false)
            onSuccess()
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit) {
        if (ui.isLoading) return
        ui = ui.copy(isLoading = true)
        viewModelScope.launch {
            delay(400)
            ui = ui.copy(isLoading = false)
            onSuccess()
        }
    }
}
