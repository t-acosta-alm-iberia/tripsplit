package com.almato.tripsplit.domain

private val AMOUNT_PATTERN = Regex("""([0-9]+)(?:\.([0-9]{1,2}))?""")

/**
 * Parses user input such as "12", "12,3" or "0.29" into exact cents.
 * Accepts a decimal comma or point, without grouping separators or fractional cents.
 * Returns null for invalid input or values that do not fit in a Long. Whether the amount is
 * positive is a business rule checked by [com.almato.tripsplit.usecase.ValidateExpense].
 */
fun parseAmountCents(text: String): Long? {
    val match = AMOUNT_PATTERN.matchEntire(text.trim().replace(',', '.')) ?: return null
    val (whole, fraction) = match.destructured
    // "12.3" -> "12" + "30": the digits are the amount in cents, without any floating point.
    return (whole + fraction.padEnd(2, '0')).toLongOrNull()
}
