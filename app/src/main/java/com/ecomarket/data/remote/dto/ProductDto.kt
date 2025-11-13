package com.ecomarket.data.remote.dto

import com.ecomarket.data.ProductEntity

data class ProductDto(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val description: String,
    val discountPercent: Int?
)

fun ProductDto.toEntity() = ProductEntity(id, name, price, imageUrl, category, description, discountPercent)
fun ProductEntity.toDto() = ProductDto(id, name, price, imageUrl, category, description, discountPercent)
