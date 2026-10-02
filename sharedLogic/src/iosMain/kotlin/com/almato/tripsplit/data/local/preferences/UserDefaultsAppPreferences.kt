package com.almato.tripsplit.data.local.preferences

import platform.Foundation.NSUserDefaults

class UserDefaultsAppPreferences: AppPreferences {

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (defaults.objectForKey(key) == null) defaultValue else defaults.boolForKey(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }

    override fun getString(key: String, defaultValue: String): String? {
        return defaults.stringForKey(key)
    }

    override fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }
}