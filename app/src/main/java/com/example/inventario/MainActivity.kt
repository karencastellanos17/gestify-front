package com.example.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.inventario.data.remote.RetrofitClient
import com.example.inventario.data.repository.AuthRepository
import com.example.inventario.ui.theme.screens.AuthScreen
import com.example.inventario.ui.theme.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar RetrofitClient con el contexto de la aplicación
        RetrofitClient.init(applicationContext)

        val authRepository = AuthRepository(applicationContext)

        setContent {
            // Verificar si ya hay un token y businessId guardados en el dispositivo
            var isAuthenticated by remember {
                mutableStateOf(authRepository.getBusinessId() != -1L)
            }

            if (!isAuthenticated) {
                AuthScreen(
                    onLoginSuccess = {
                        isAuthenticated = true
                    }
                )
            } else {
                MainScreen(
                    onLogout = {
                        authRepository.logout()
                        isAuthenticated = false
                    }
                )
            }
        }
    }
}
