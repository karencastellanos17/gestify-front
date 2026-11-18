package com.example.inventario

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.data.remote.RetrofitClient
import com.example.inventario.data.remote.SessionManager
import com.example.inventario.data.repository.AuthRepository
import com.example.inventario.ui.theme.screens.AuthScreen
import com.example.inventario.ui.theme.screens.MainScreen
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitClient.init(applicationContext)

        val authRepository = AuthRepository(applicationContext)

        setContent {
            val viewModel: GestifyViewModel = viewModel()

            var isAuthenticated by remember {
                mutableStateOf(authRepository.getBusinessId() != -1L)
            }

            val sessionExpired by SessionManager.expired.collectAsState()
            LaunchedEffect(sessionExpired) {
                if (sessionExpired) {
                    viewModel.logout()
                    isAuthenticated = false
                    SessionManager.reset()
                    Toast.makeText(
                        this@MainActivity,
                        "Tu sesión expiró. Inicia sesión de nuevo.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            if (!isAuthenticated) {
                AuthScreen(
                    onLoginSuccess = {
                        isAuthenticated = true
                    },
                    viewModel = viewModel
                )
            } else {
                MainScreen(
                    onLogout = {
                        authRepository.logout()
                        isAuthenticated = false
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}