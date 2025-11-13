package com.ecomarket.di

import com.ecomarket.data.remote.api.ExternalApi
import com.ecomarket.data.remote.api.ProductsApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {

    // Backend local (tu API de productos)
    private const val PRODUCTS_BASE_URL = "http://10.0.2.2:8080/"

    // API externa de tipo de cambio (open.er-api.com)
    private const val RATES_BASE_URL = "https://open.er-api.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Moshi con soporte para data classes de Kotlin
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun retrofit(url: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttp)
            .build()

    // --- APIs ---

    // Tu backend de productos (CRUD, etc.)
    val productsApi: ProductsApi by lazy {
        retrofit(PRODUCTS_BASE_URL).create(ProductsApi::class.java)
    }

    // API externa de tasas de cambio USD -> CLP
    val externalApi: ExternalApi by lazy {
        retrofit(RATES_BASE_URL).create(ExternalApi::class.java)
    }
}
