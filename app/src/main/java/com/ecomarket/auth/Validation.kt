package com.ecomarket.auth

private val EMAIL_REGEX =
    Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

object Validation {

    fun emailErrorOrNull(email: String): String? {
        if (email.isBlank()) return "Ingresa tu correo"
        if (!EMAIL_REGEX.matches(email)) return "Formato de correo no válido"
        return null
    }

    fun passwordErrorOrNull(password: String): String? {
        if (password.isBlank()) return "Ingresa tu contraseña"
        if (password.length < 6) return "Mínimo 6 caracteres"
        return null
    }
}
