package com.almato.tripsplit.data.local.preferences

/** Overview screen settings; keys and defaults are defined once for both platforms. */
class OverviewPreferences(private val store: AppPreferences) {
    var showExpenses: Boolean
        get() = store.getBoolean(KEY_SHOW_EXPENSES, defaultValue = true)
        set(value) = store.putBoolean(KEY_SHOW_EXPENSES, value)

    private companion object {
        const val KEY_SHOW_EXPENSES = "show_expenses"
    }
}
