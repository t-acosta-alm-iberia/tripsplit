package com.almato.tripsplit.ui

import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.currentLocale

//Option 1
//Q&A: Why can I use iOS specific APIs, in Kotlin?
actual fun formatEuros(cents: Long): String {
    val formatter = NSNumberFormatter().apply {
        locale = NSLocale.currentLocale
        numberStyle = NSNumberFormatterDecimalStyle
        minimumFractionDigits = 2u
        maximumFractionDigits = 2u
    }

    val amount = NSDecimalNumber(
        mantissa = if (cents < 0) 0uL - cents.toULong() else cents.toULong(),
        exponent = -2,
        isNegative = cents < 0,
    )

    return "${formatter.stringFromNumber(amount) ?: amount.stringValue}\u00A0€"
}