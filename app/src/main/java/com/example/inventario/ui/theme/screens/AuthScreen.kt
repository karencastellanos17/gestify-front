package com.example.inventario.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventario.ui.theme.Temas.InventarioTheme
import com.example.inventario.ui.theme.components.BrandBackdrop
import com.example.inventario.ui.theme.components.GestifyMark
import com.example.inventario.ui.theme.components.InventoryLogo
import com.example.inventario.ui.theme.viewmodel.GestifyViewModel
import com.example.inventario.utils.AppThemeColor
import com.example.inventario.utils.ValidationUtils

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: GestifyViewModel = viewModel()
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    val currentThemeColor by remember { mutableStateOf(AppThemeColor.BLUE) }

    val emailError = ValidationUtils.validateEmail(email)
    val passwordError = ValidationUtils.validatePassword(password)
    val businessError = if (isLoginMode) null else ValidationUtils.validatePersonOrCompanyName(businessName, "El nombre del negocio")

    val isFormValid = emailError == null && passwordError == null && businessError == null
    val isWideScreen = LocalConfiguration.current.screenWidthDp >= 800

    InventarioTheme(selectedColor = currentThemeColor) {
        val colors = MaterialTheme.colorScheme

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            if (isWideScreen) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Brush.linearGradient(listOf(colors.primary, colors.secondary)))
                ) {
                    BrandBackdrop(
                        modifier = Modifier.fillMaxSize(),
                        tint = colors.onPrimary
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 56.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InventoryLogo(logoSize = 120.dp)

                        Text(
                            text = "Gestify",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = FontFamily.Cursive,
                                fontSize = 104.sp,
                                lineHeight = 112.sp,
                                letterSpacing = 0.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(4.dp)
                                    .background(colors.onPrimary.copy(alpha = 0.85f), RoundedCornerShape(50))
                            )
                            Text(
                                text = "Gestionar mejor, crecer más",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 34.sp,
                                    lineHeight = 42.sp
                                ),
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Italic,
                                color = colors.onPrimary
                            )
                        }

                        Text(
                            text = "Controla tu stock, tus ventas y tus alertas desde un solo lugar.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 22.sp,
                                lineHeight = 32.sp
                            ),
                            color = colors.onPrimary.copy(alpha = 0.92f),
                            modifier = Modifier.widthIn(max = 520.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        listOf(
                            "Inventario siempre actualizado",
                            "Ventas y caja en tiempo real",
                            "Alertas de stock y proveedor recomendado"
                        ).forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = colors.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                                    color = colors.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .statusBarsPadding()
                    .imePadding(),
                contentAlignment = Alignment.Center
            ) {
                BrandBackdrop(
                    modifier = Modifier.fillMaxSize(),
                    tint = colors.primary,
                    scale = 0.8f,
                    alphaScale = 0.45f
                )

                Card(
                    modifier = Modifier
                        .widthIn(max = 440.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.4f)),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GestifyMark(size = 44.dp)
                            Text(
                                text = "Gestify",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 44.sp,
                                    lineHeight = 50.sp,
                                    letterSpacing = 0.sp,
                                    brush = Brush.linearGradient(listOf(colors.primary, colors.secondary))
                                ),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (isLoginMode) "Bienvenido de nuevo" else "Crea tu cuenta",
                                style = MaterialTheme.typography.headlineSmall,
                                color = colors.onSurface
                            )
                            Text(
                                text = if (isLoginMode) "Acceso a tu Inventario" else "Registra tu Emprendimiento",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )
                        }

                        viewModel.errorMessage?.let { error ->
                            Surface(
                                color = colors.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = error,
                                    color = colors.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        if (!isLoginMode) {
                            OutlinedTextField(
                                value = businessName,
                                onValueChange = { if (it.length <= 60) businessName = it },
                                label = { Text("Nombre del Negocio / Tienda *") },
                                isError = showErrors && businessError != null,
                                supportingText = if (showErrors && businessError != null) {
                                    { Text(businessError) }
                                } else null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it.trim().lowercase() },
                            label = { Text("Correo Electrónico *") },
                            placeholder = { Text("ejemplo@gmail.com") },
                            isError = showErrors && emailError != null,
                            supportingText = if (showErrors && emailError != null) {
                                { Text(emailError) }
                            } else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { if (it.length <= 40) password = it },
                            label = { Text("Contraseña *") },
                            isError = showErrors && passwordError != null,
                            supportingText = if (showErrors && passwordError != null) {
                                { Text(passwordError) }
                            } else {
                                { Text("Mínimo 6 caracteres y al menos un número") }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                showErrors = true
                                if (isFormValid) {
                                    if (isLoginMode) {
                                        viewModel.login(email.trim(), password) {
                                            onLoginSuccess()
                                        }
                                    } else {
                                        viewModel.register(businessName.trim(), email.trim(), password) {
                                            onLoginSuccess()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            enabled = !viewModel.isLoading
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = colors.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (isLoginMode) "Iniciar Sesión" else "Crear Cuenta",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                isLoginMode = !isLoginMode
                                showErrors = false
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = if (isLoginMode) "¿No tienes cuenta? Regístrate aquí" else "¿Ya tienes cuenta? Inicia sesión",
                                color = colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}