package com.ecomarket.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ecomarket.data.user.UserDao
import com.ecomarket.data.user.UserEntity

@Database(
    entities = [ProductEntity::class, CartItemEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class EcoDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: EcoDatabase? = null

        fun getInstance(context: Context): EcoDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, EcoDatabase::class.java, "eco.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
