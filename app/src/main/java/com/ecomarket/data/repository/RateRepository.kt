package com.ecomarket.data.repository

import com.ecomarket.data.remote.api.ExternalApi

class RateRepository(
    private val api: ExternalApi
) {
    suspend fun usdToClp(): Double {
        val response = api.getLatestRates()

        if (response.result != "success") {
            error("API devolvió estado inválido: ${response.result}")
        }

        return response.rates["CLP"]
            ?: error("No se encontró CLP en 'rates'")
    }
}
