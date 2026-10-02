package com.almato.tripsplit.data.local.preferences

interface AppPreferences {
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getString(key: String, defaultValue: String): String?
    fun putString(key: String, value: String)
}