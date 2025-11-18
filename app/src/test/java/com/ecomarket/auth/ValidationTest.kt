package com.ecomarket.auth

import org.junit.Assert.*
import org.junit.Test

class ValidationTest {

    @Test
    fun `emailErrorOrNull devuelve error si el email está vacío`() {
        val email = ""

        val error = Validation.emailErrorOrNull(email)

        assertNotNull("El error no debería ser nulo", error)
        assertEquals("Ingresa tu correo***", error)
    }

    @Test
    fun `emailErrorOrNull devuelve error si el email no tiene formato válido`() {
        val email = "esto-no-es-un-email"
        val error = Validation.emailErrorOrNull(email)
        assertNotNull(error)
        assertEquals("Formato de correo no válido", error)
    }

    @Test
    fun `emailErrorOrNull devuelve null si el email es válido`() {
        val email = "usuario@dominio.com"
        val error = Validation.emailErrorOrNull(email)
        assertNull("El error debería ser nulo para un email válido", error)
    }


    @Test
    fun `passwordErrorOrNull devuelve error si la contraseña está vacía`() {
        val password = ""
        val error = Validation.passwordErrorOrNull(password)
        assertNotNull(error)
        assertEquals("Ingresa tu contraseña", error)
    }

    @Test
    fun `passwordErrorOrNull devuelve error si la contraseña es muy corta`() {
        val password = "12345" // 5 caracteres
        val error = Validation.passwordErrorOrNull(password)
        assertNotNull(error)
        assertEquals("Mínimo 6 caracteres", error)
    }

    @Test
    fun `passwordErrorOrNull devuelve null si la contraseña es válida`() {
        val password = "123456" // 6 caracteres
        val error = Validation.passwordErrorOrNull(password)
        assertNull("El error debería ser nulo para una contraseña válida", error)
    }
}