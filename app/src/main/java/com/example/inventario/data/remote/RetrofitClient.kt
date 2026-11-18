package com.example.inventario.data.remote

import android.content.Context
import android.content.pm.ApplicationInfo
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://gestify-5xyc.onrender.com/"

    private var tokenManager: TokenManager? = null
    private var instance: Retrofit? = null

    fun init(context: Context) {
        val appContext = context.applicationContext
        tokenManager = TokenManager(appContext)
        val isDebuggable = (appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        instance = buildRetrofit(isDebuggable)
    }

    val retrofit: Retrofit
        get() = instance ?: error("RetrofitClient.init(context) no fue llamado")

    private fun buildRetrofit(isDebuggable: Boolean): Retrofit {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            val token = tokenManager?.getToken()

            val builder = request.newBuilder()
            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }

            val response = chain.proceed(builder.build())

            val isAuthEndpoint = request.url.encodedPath.startsWith("/api/auth/")
            if (response.code == 401 && !token.isNullOrBlank() && !isAuthEndpoint) {
                tokenManager?.clear()
                SessionManager.notifyExpired()
            }
            response
        }

        val logging = HttpLoggingInterceptor().apply {
            level = if (isDebuggable) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            redactHeader("Authorization")
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}