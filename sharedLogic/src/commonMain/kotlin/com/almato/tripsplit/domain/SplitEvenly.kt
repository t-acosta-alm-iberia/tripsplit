package com.almato.tripsplit.domain

/**
 * Splits [amountCents] equally between [participantIds]. Leftover cents go one each to the
 * first IDs in sorted order, so the result is independent of selection order in either UI
 * and the shares always add up to [amountCents].
 */
fun splitEvenly(amountCents: Long, participantIds: Collection<String>): Map<String, Long> {
    require(participantIds.isNotEmpty()) { "Select at least one participant" }
    val sortedIds = participantIds.sorted()
    val baseShareCents = amountCents / sortedIds.size
    val leftoverCents = amountCents % sortedIds.size
    return sortedIds.withIndex().associate { (index, id) ->
        id to baseShareCents + if (index.toLong() < leftoverCents) 1L else 0L
    }
}
