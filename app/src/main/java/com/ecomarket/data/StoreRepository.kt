package com.ecomarket.data

import com.ecomarket.data.remote.dto.toDto
import com.ecomarket.data.remote.dto.toEntity
import com.ecomarket.data.user.UserDao
import com.ecomarket.data.user.UserEntity
import com.ecomarket.data.user.UserRole
import com.ecomarket.di.Graph
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Exception

class StoreRepository(
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val userDao: UserDao
) {
    // Productos (se queda igual)
    fun observeProducts(): Flow<List<ProductEntity>> = productDao.getAll()
    fun observeProduct(id: String): Flow<ProductEntity?> = productDao.getById(id)

    suspend fun ensureSeed() {
        if (productDao.count() == 0) {
            productDao.upsertAll(Seed.products())
        }
    }

    // Carrito (se queda igual)
    fun observeCart(): Flow<List<CartLine>> = cartDao.observeCart()
    fun observeSubtotal(): Flow<Double> = cartDao.observeSubtotal().map { it ?: 0.0 }

    suspend fun addToCart(productId: String) {
        val rows = cartDao.changeQty(productId, +1)
        if (rows == 0) cartDao.upsert(CartItemEntity(productId, 1))
    }

    suspend fun increase(productId: String) {
        val rows = cartDao.changeQty(productId, +1)
        if (rows == 0) cartDao.upsert(CartItemEntity(productId, 1))
    }

    suspend fun decrease(productId: String) {
        cartDao.changeQty(productId, -1)
        cartDao.pruneZeros()
    }

    suspend fun remove(productId: String) = cartDao.remove(productId)
    suspend fun clearCart() = cartDao.clear()

    // Usuario (se queda igual)
    suspend fun findUserByEmail(email: String): UserEntity? {
        return userDao.findByEmail(email)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
    }
    // Función para crear usuarios de prueba si no existen
    suspend fun ensureUsersSeed() {
        if (userDao.findByEmail("admin@ecomarket.cl") == null) {
            userDao.insert(
                UserEntity(
                    email = "admin@ecomarket.cl",
                    passHash = "admin123", // Simulación, en un proyecto real esto debe ser un hash!
                    role = UserRole.ADMIN,
                    name = "Admin EcoMarket"
                )
            )
        }
        if (userDao.findByEmail("cliente@ecomarket.cl") == null) {
            userDao.insert(
                UserEntity(
                    email = "cliente@ecomarket.cl",
                    passHash = "cliente123",
                    role = UserRole.CUSTOMER,
                    name = "Juan Cliente",
                    birthDate = "1995-05-20",
                    shippingAddress = "Av. Siempre Viva 742"
                )
            )
        }
    }

    // --- Funciones CRUD para Productos (Admin) ---
    suspend fun getProductById(id: String): ProductEntity? {
        return productDao.getProductById(id)
    }

    // --- INICIO DE LA MODIFICACIÓN ---

    suspend fun addProduct(product: ProductEntity) {
        // Obtenemos la API desde el Graph
        val api = Graph.productsApi
        try {
            // 1. Convertimos la entidad local a un DTO y la enviamos al servidor
            val createdDto = api.create(product.toDto())

            // 2. Guardamos la respuesta del servidor (que tiene el ID correcto) en Room
            productDao.insert(createdDto.toEntity())

        } catch (e: Exception) {
            // El ViewModel (ProductEditViewModel) tiene un try-catch,
            // así que relanzamos la excepción para que pueda mostrar el error en la UI.
            throw e
        }
    }

    suspend fun updateProduct(product: ProductEntity) {
        val api = Graph.productsApi
        try {
            // 1. Enviamos la entidad actualizada a la API
            val updatedDto = api.update(product.id, product.toDto())

            // 2. Guardamos la respuesta actualizada en Room
            productDao.update(updatedDto.toEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteProduct(product: ProductEntity) {
        val api = Graph.productsApi
        try {
            // 1. Primero lo borramos de la API
            api.delete(product.id)

            // 2. Si la API tuvo éxito, lo borramos de Room
            productDao.delete(product)
        } catch (e: Exception) {
            throw e
        }
    }
    // --- FIN DE LA MODIFICACIÓN ---
}