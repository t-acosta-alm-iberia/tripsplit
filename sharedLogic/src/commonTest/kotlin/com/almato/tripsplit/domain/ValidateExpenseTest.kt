package com.almato.tripsplit.domain

import com.almato.tripsplit.usecase.ExpenseValidationError
import com.almato.tripsplit.usecase.ValidateExpense
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidateExpenseTest {
    private val validateExpense = ValidateExpense()
    private val trip = setOf("1", "2", "3")

    @Test fun acceptsValidExpense() {
        assertNull(validate())
    }
    @Test fun reportsEachRule() {
        assertEquals(ExpenseValidationError.BLANK_DESCRIPTION, validate(description = "  "))
        assertEquals(ExpenseValidationError.INVALID_AMOUNT, validate(amountCents = null))
        assertEquals(ExpenseValidationError.NON_POSITIVE_AMOUNT, validate(amountCents = 0))
        assertEquals(ExpenseValidationError.UNKNOWN_PAYER, validate(payer = "missing"))
        assertEquals(ExpenseValidationError.UNKNOWN_PAYER, validate(payer = null))
        assertEquals(ExpenseValidationError.NO_PARTICIPANTS, validate(split = emptySet()))
        assertEquals(ExpenseValidationError.UNKNOWN_PARTICIPANT, validate(split = setOf("1", "missing")))
    }
    @Test fun reportsFirstFailureInFormOrder() {
        assertEquals(ExpenseValidationError.BLANK_DESCRIPTION, validate(description = "", amountCents = null))
    }

    private fun validate(
        description: String = "Dinner",
        amountCents: Long? = 1000,
        payer: String? = "1",
        split: Set<String> = trip,
    ) = validateExpense(description, amountCents, payer, split, trip)
}
