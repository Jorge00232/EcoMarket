package com.ecomarket.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreRepository(
    private val productDao: ProductDao,
    private val cartDao: CartDao
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
}
