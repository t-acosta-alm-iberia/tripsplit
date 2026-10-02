package com.almato.tripsplit.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SplitEvenlyTest {
    @Test fun givesLeftoverCentsToFirstSortedIds() {
        assertEquals(mapOf("a" to 334L, "b" to 334L, "c" to 333L), splitEvenly(1001, listOf("c", "a", "b")))
    }
    @Test fun sharesAlwaysAddUpToTheAmount() {
        for (amount in listOf(1L, 99L, 1000L, 1003L)) {
            for (people in 1..7) {
                val shares = splitEvenly(amount, (1..people).map { "p$it" })
                assertEquals(amount, shares.values.sum(), "amount=$amount people=$people")
            }
        }
    }
    @Test fun rejectsEmptySplit() {
        assertFailsWith<IllegalArgumentException> { splitEvenly(100, emptyList()) }
    }
}
