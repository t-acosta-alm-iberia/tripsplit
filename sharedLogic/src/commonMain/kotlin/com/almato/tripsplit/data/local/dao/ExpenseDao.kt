package com.almato.tripsplit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.almato.tripsplit.data.local.entity.ExpenseEntity
import com.almato.tripsplit.data.local.entity.ExpenseParticipantEntity


@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert
    suspend fun insertParticipantsLinks(
        links: List<ExpenseParticipantEntity>
    )

    @Transaction
    suspend fun insertExpensesWithParticipants(
        expense: ExpenseEntity,
        links: List<ExpenseParticipantEntity>
    ) {
        insertExpense(expense)
        insertParticipantsLinks(links)
    }
}