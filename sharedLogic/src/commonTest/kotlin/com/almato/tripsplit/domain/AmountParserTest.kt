package com.almato.tripsplit.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AmountParserTest {
    @Test fun acceptsExactCentsAndDecimalComma() {
        assertEquals(29L, parseAmountCents("0.29"))
        assertEquals(1230L, parseAmountCents(" 12,3 "))
        assertEquals(1200L, parseAmountCents("12"))
        assertEquals(0L, parseAmountCents("0"))
        assertEquals(Long.MAX_VALUE, parseAmountCents("92233720368547758.07"))
    }
    @Test fun rejectsInvalidAmountsAndOverflow() {
        listOf("", "-1", "1.001", "1,000.00", "1e3", "NaN", "12.", "92233720368547758.08").forEach {
            assertNull(parseAmountCents(it), it)
        }
    }
}
