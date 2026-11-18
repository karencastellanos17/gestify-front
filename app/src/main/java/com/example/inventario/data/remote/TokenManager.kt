package com.example.inventario.data.remote

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("gestify_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_BUSINESS_ID = "business_id"
        private const val KEY_THEME_COLOR = "theme_color"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val DEFAULT_THEME = "BLUE"
    }

    fun saveAuthData(token: String, businessId: Long, themeColor: String?, isDarkMode: Boolean) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_BUSINESS_ID, businessId)
            .putString(KEY_THEME_COLOR, themeColor ?: DEFAULT_THEME)
            .putBoolean(KEY_DARK_MODE, isDarkMode)
            .apply()
    }

    fun saveTheme(themeColor: String, isDarkMode: Boolean) {
        prefs.edit()
            .putString(KEY_THEME_COLOR, themeColor)
            .putBoolean(KEY_DARK_MODE, isDarkMode)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getBusinessId(): Long = prefs.getLong(KEY_BUSINESS_ID, -1L)

    fun getThemeColor(): String = prefs.getString(KEY_THEME_COLOR, DEFAULT_THEME) ?: DEFAULT_THEME

    fun getDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)

    fun clear() {
        prefs.edit().clear().apply()
    }
}