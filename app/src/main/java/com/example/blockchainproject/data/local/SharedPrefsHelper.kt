package com.example.blockchainproject.data.local

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SAVED_ADDRESS = "saved_tron_address"
    }

    fun saveAddress(address: String) {
        prefs.edit().putString(KEY_SAVED_ADDRESS, address).apply()
    }

    fun getSavedAddress(): String? {
        return prefs.getString(KEY_SAVED_ADDRESS, null)
    }

    fun clearAddress() {
        prefs.edit().remove(KEY_SAVED_ADDRESS).apply()
    }
}
