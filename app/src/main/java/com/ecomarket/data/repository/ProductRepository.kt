package com.ecomarket.data.repository

import com.ecomarket.data.ProductDao
import com.ecomarket.data.ProductEntity
import com.ecomarket.data.remote.api.ProductsApi
import com.ecomarket.data.remote.dto.toDto
import com.ecomarket.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val dao: ProductDao,
    private val api: ProductsApi
) {
    // --- Lecturas reactivas para UI ---
    fun observeAll(): Flow<List<ProductEntity>> = dao.getAll()
    fun observeById(id: String): Flow<ProductEntity?> = dao.getById(id)

    // --- Sincronización completa desde el servidor ---
    suspend fun refresh() {
        val remote = api.getAll()
        dao.clearAll()
        dao.upsertAll(remote.map { it.toEntity() })
    }

    // --- CRUD con sincronización API + Room ---
    suspend fun create(local: ProductEntity) {
        val created = api.create(local.toDto())
        dao.insert(created.toEntity())
    }

    suspend fun update(local: ProductEntity) {
        val updated = api.update(local.id, local.toDto())
        dao.update(updated.toEntity())
    }

    suspend fun delete(id: String) {
        api.delete(id)
        // si prefieres, primero borra local y luego pega a red
        dao.getProductById(id)?.let { dao.delete(it) }
    }

    // --- Utilidades ---
    suspend fun isEmpty(): Boolean = dao.count() == 0
}
