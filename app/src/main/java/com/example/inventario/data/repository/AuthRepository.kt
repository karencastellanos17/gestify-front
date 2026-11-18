package com.example.inventario.data.repository

import android.content.Context
import com.example.inventario.data.remote.ApiException
import com.example.inventario.data.remote.AuthResponse
import com.example.inventario.data.remote.GestifyApi
import com.example.inventario.data.remote.LoginRequest
import com.example.inventario.data.remote.NETWORK_ERROR_MESSAGE
import com.example.inventario.data.remote.RegisterRequest
import com.example.inventario.data.remote.RetrofitClient
import com.example.inventario.data.remote.TokenManager
import com.example.inventario.data.remote.toApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class AuthRepository(context: Context) {
    private val api = RetrofitClient.retrofit.create(GestifyApi::class.java)
    private val tokenManager = TokenManager(context)

    suspend fun login(email: String, pass: String): Result<AuthResponse> =
        authCall { api.login(LoginRequest(email, pass)) }

    suspend fun register(businessName: String, email: String, pass: String): Result<AuthResponse> =
        authCall { api.register(RegisterRequest(businessName, email, pass)) }

    private suspend fun authCall(
        block: suspend () -> retrofit2.Response<AuthResponse>
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = block()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                tokenManager.saveAuthData(body.token, body.idNegocio, body.themeColor, body.isDarkMode)
                Result.success(body)
            } else if (response.isSuccessful) {
                Result.failure(ApiException(response.code(), "El servidor devolvió una respuesta vacía."))
            } else {
                Result.failure(response.toApiException())
            }
        } catch (_: IOException) {
            Result.failure(ApiException(0, NETWORK_ERROR_MESSAGE))
        } catch (e: Exception) {
            Result.failure(ApiException(0, e.localizedMessage ?: "Error inesperado."))
        }
    }

    fun logout() {
        tokenManager.clear()
    }

    fun getBusinessId(): Long = tokenManager.getBusinessId()

    fun getThemeColor(): String = tokenManager.getThemeColor()
    fun getDarkMode(): Boolean = tokenManager.getDarkMode()
    fun saveTheme(themeColor: String, isDarkMode: Boolean) = tokenManager.saveTheme(themeColor, isDarkMode)
}