package com.almato.tripsplit.ui

import java.math.BigDecimal
import java.text.NumberFormat

actual fun formatEuros(cents: Long): String {
    val formatter = NumberFormat.getNumberInstance().apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    return "${formatter.format(BigDecimal.valueOf(cents, 2))}\u00A0€"
}