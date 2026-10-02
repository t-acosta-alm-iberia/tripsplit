package com.almato.tripsplit.usecase

enum class ExpenseValidationError {
    BLANK_DESCRIPTION,
    INVALID_AMOUNT,
    NON_POSITIVE_AMOUNT,
    UNKNOWN_PAYER,
    NO_PARTICIPANTS,
    UNKNOWN_PARTICIPANT,
}

/**
 * Business rules for a new expense. Returns the first failure in form order, or null when valid.
 * Platforms map failures to localized messages and keep their own loading/saving guards.
 */
class ValidateExpense {
    operator fun invoke(
        description: String,
        amountCents: Long?,
        paidByParticipantId: String?,
        splitParticipantIds: Set<String>,
        tripParticipantIds: Set<String>,
    ): ExpenseValidationError? = when {
        description.isBlank() -> ExpenseValidationError.BLANK_DESCRIPTION
        amountCents == null -> ExpenseValidationError.INVALID_AMOUNT
        amountCents <= 0 -> ExpenseValidationError.NON_POSITIVE_AMOUNT
        paidByParticipantId !in tripParticipantIds -> ExpenseValidationError.UNKNOWN_PAYER
        splitParticipantIds.isEmpty() -> ExpenseValidationError.NO_PARTICIPANTS
        !tripParticipantIds.containsAll(splitParticipantIds) -> ExpenseValidationError.UNKNOWN_PARTICIPANT
        else -> null
    }
}
