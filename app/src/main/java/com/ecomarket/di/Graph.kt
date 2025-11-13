package com.ecomarket.di

import android.content.Context
import com.ecomarket.data.EcoDatabase
import com.ecomarket.data.StoreRepository
import com.ecomarket.data.remote.api.ProductsApi
import com.ecomarket.data.user.UserEntity

object Graph {
    // --- Lo que ya tenías ---
    lateinit var database: EcoDatabase
        private set
    lateinit var repository: StoreRepository
        private set
    var loggedInUser: UserEntity? = null
        private set

    // --- Nuevo: API para sincronización opcional ---
    lateinit var productsApi: ProductsApi
        private set

    fun provide(context: Context) {
        database = EcoDatabase.getInstance(context)
        repository = StoreRepository(database.productDao(), database.cartDao(), database.userDao())

        // Retrofit disponible sin tocar tu StoreRepository
        productsApi = NetworkModule.productsApi
    }

    fun login(user: UserEntity) { loggedInUser = user }
    fun logout() { loggedInUser = null }
}
