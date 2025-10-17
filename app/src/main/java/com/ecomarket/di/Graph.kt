package com.ecomarket.di

import android.content.Context
import com.ecomarket.data.EcoDatabase
import com.ecomarket.data.StoreRepository
import com.ecomarket.data.user.UserEntity

object Graph {
    lateinit var database: EcoDatabase
        private set
    lateinit var repository: StoreRepository
        private set
    var loggedInUser: UserEntity? = null
        private set
    fun provide(context: Context) {
        database = EcoDatabase.getInstance(context)
        repository = StoreRepository(database.productDao(), database.cartDao(), database.userDao())
    }
    fun login(user: UserEntity) {
        loggedInUser = user
    }

    fun logout() {
        loggedInUser = null
    }
}
