package com.almato.tripsplit.data.local.preferences

import android.content.Context
import androidx.core.content.edit

class SharedPreferencesAppPreferences(
    contex: Context
) : AppPreferences {
    val preferences = contex.getSharedPreferences("tripsplit_preferences", Context.MODE_PRIVATE)


    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        preferences.edit { this.putBoolean(key, value) }
    }

    override fun getString(key: String, defaultValue: String): String? {
        return preferences.getString(key, defaultValue)
    }

    override fun putString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

}