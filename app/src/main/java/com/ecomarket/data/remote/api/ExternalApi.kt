package com.ecomarket.data.remote.api

import com.ecomarket.data.remote.dto.RateResponse
import retrofit2.http.GET

interface ExternalApi {

    // Este endpoint coincide EXACTAMENTE con:
    // https://open.er-api.com/v6/latest/USD
    @GET("v6/latest/USD")
    suspend fun getLatestRates(): RateResponse
}
