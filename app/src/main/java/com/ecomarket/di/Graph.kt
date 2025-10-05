package com.ecomarket.di

import android.content.Context
import com.ecomarket.data.EcoDatabase
import com.ecomarket.data.StoreRepository

object Graph {
    lateinit var database: EcoDatabase
        private set
    lateinit var repository: StoreRepository
        private set

    fun provide(context: Context) {
        database = EcoDatabase.getInstance(context)
        repository = StoreRepository(database.productDao(), database.cartDao())
    }
}
