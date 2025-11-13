package com.ecomarket.util

import com.ecomarket.data.ProductEntity
import java.text.NumberFormat
import java.util.Locale

fun Double.asCLP(): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return nf.format(this)
}

fun ProductEntity.finalPrice(): Double =
    if (discountPercent != null) price * (1 - discountPercent / 100.0) else price

// NUEVO → para mostrar precios en dólares
fun Double.asUSD(): String {
    val nf = NumberFormat.getCurrencyInstance(Locale.US)
    return nf.format(this)
}

fun ProductEntity.priceInUSD(usdToClp: Double?): Double? {
    if (usdToClp == null || usdToClp == 0.0) return null
    return finalPrice() / usdToClp
}
