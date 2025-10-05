package com.ecomarket.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val description: String,
    val discountPercent: Int? // null = sin descuento
)
