package com.example.inventario.data.remote

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gestify_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_BUSINESS_ID = "business_id"
    }

    fun saveAuthData(token: String, businessId: Long) {
        prefs.edit().putString(KEY_TOKEN, token).putLong(KEY_BUSINESS_ID, businessId).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getBusinessId(): Long = prefs.getLong(KEY_BUSINESS_ID, -1L)

    fun clear() {
        prefs.edit().clear().apply()
    }
}