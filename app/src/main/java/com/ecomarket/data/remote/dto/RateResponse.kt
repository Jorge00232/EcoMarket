package com.ecomarket.data.remote.dto

data class RateResponse(
    val result: String,
    val rates: Map<String, Double>
)
