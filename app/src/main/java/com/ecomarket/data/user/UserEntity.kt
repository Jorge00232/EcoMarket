package com.ecomarket.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

// Enum para definir los roles de forma segura. ¡Mucho mejor que usar Strings!
enum class UserRole {
    ADMIN,
    CUSTOMER
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val passHash: String, // Guardaremos un "hash" de la contraseña, no el texto plano
    val role: UserRole,
    // Datos adicionales para el perfil del cliente
    val name: String? = null,
    val birthDate: String? = null, // Formato "AAAA-MM-DD"
    val shippingAddress: String? = null
)