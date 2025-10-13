package com.ecomarket.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.ecomarket.data.user.UserDao
import com.ecomarket.data.user.UserEntity
import com.ecomarket.data.user.UserRole
class StoreRepository(
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val userDao: UserDao
) {
    // Productos
    fun observeProducts(): Flow<List<ProductEntity>> = productDao.getAll()
    fun observeProduct(id: String): Flow<ProductEntity?> = productDao.getById(id)

    suspend fun ensureSeed() {
        if (productDao.count() == 0) {
            productDao.upsertAll(Seed.products())
        }
    }

    // Carrito
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

    // Usuario
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
}
