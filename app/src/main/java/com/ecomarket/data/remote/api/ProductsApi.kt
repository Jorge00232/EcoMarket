package com.ecomarket.data.remote.api

import com.ecomarket.data.remote.dto.ProductDto
import retrofit2.http.*

interface ProductsApi {
    @GET("/products") suspend fun getAll(): List<ProductDto>
    @GET("/products/{id}") suspend fun getById(@Path("id") id: String): ProductDto
    @POST("/products") suspend fun create(@Body body: ProductDto): ProductDto
    @PUT("/products/{id}") suspend fun update(@Path("id") id: String, @Body body: ProductDto): ProductDto
    @DELETE("/products/{id}") suspend fun delete(@Path("id") id: String)
}
