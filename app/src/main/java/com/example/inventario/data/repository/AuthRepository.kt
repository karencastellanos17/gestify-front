package com.example.inventario.data.repository


import android.content.Context
import com.example.inventario.data.remote.AuthResponse
import com.example.inventario.data.remote.GestifyApi
import com.example.inventario.data.remote.LoginRequest
import com.example.inventario.data.remote.RegisterRequest
import com.example.inventario.data.remote.RetrofitClient
import com.example.inventario.data.remote.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(context: Context) {
    private val api = RetrofitClient.retrofit.create(GestifyApi::class.java)
    private val tokenManager = TokenManager(context)

    suspend fun login(email: String, pass: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {
                val authData = response.body()!!
                tokenManager.saveAuthData(authData.token, authData.idNegocio)
                Result.success(authData)
            } else {
                Result.failure(Exception("Credenciales incorrectas o error en el servidor"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(businessName: String, email: String, pass: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.register(RegisterRequest(businessName, email, pass))
            if (response.isSuccessful && response.body() != null) {
                val authData = response.body()!!
                tokenManager.saveAuthData(authData.token, authData.idNegocio)
                Result.success(authData)
            } else {
                Result.failure(Exception("No se pudo registrar el negocio"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clear()
    }

    fun getBusinessId(): Long = tokenManager.getBusinessId()
}