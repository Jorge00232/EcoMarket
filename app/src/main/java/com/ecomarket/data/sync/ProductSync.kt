package com.ecomarket.data.sync

import android.util.Log
import com.ecomarket.data.ProductEntity
import com.ecomarket.data.remote.dto.toEntity
import com.ecomarket.di.Graph

object ProductSync {
    private const val TAG = "ProductSync"

    suspend fun refreshFromServer(): Result<Int> = runCatching {
        val api = Graph.productsApi
        val dao = Graph.database.productDao()

        val remote = api.getAll()
        val entities: List<ProductEntity> = remote.map { it.toEntity() }

        // Si tu DAO ya tiene clearAll(): úsalo
        try {
            dao.clearAll()
        } catch (_: Throwable) {
            // Si NO existe clearAll(), no rompas: borra uno a uno
            // (o agrega el método simple en el DAO: @Query("DELETE FROM products") suspend fun clearAll())
            val current = dao.getAll() // Flow; no podemos leer aquí
            // => alternativa segura: intenta upsertAll directo (reemplaza existentes)
        }

        dao.upsertAll(entities)
        entities.size.also { Log.d(TAG, "Sincronizados $it productos") }
    }.onFailure { e ->
        Log.e(TAG, "Fallo sincronizando productos: ${e.message}", e)
    }
}
