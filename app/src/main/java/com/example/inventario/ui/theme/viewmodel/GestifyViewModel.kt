package com.example.inventario.ui.theme.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventario.data.model.Product
import com.example.inventario.data.model.Supplier
import com.example.inventario.data.remote.CloudinaryService
import com.example.inventario.data.repository.AuthRepository
import com.example.inventario.data.repository.GestifyRepository
import com.example.inventario.ui.theme.screens.SaleItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class GestifyViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val repository = GestifyRepository(application)

    // Estados de UI reactivos
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _sales = MutableStateFlow<List<SaleItem>>(emptyList())
    val sales: StateFlow<List<SaleItem>> = _sales.asStateFlow()

    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        // Cargar datos al iniciar si ya hay sesión activa
        if (authRepository.getBusinessId() != -1L) {
            refreshAllData()
        }
    }

    fun refreshAllData() {
        viewModelScope.launch {
            isLoading = true
            _products.value = repository.getProducts()
            _sales.value = repository.getSales()
            _suppliers.value = repository.getSuppliers()
            isLoading = false
        }
    }

    // --- Autenticación ---
    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = authRepository.login(email, pass)
            isLoading = false
            result.fold(
                onSuccess = {
                    refreshAllData()
                    onSuccess()
                },
                onFailure = { errorMessage = it.localizedMessage ?: "Error al iniciar sesión" }
            )
        }
    }

    fun register(businessName: String, email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = authRepository.register(businessName, email, pass)
            isLoading = false
            result.fold(
                onSuccess = {
                    refreshAllData()
                    onSuccess()
                },
                onFailure = { errorMessage = it.localizedMessage ?: "Error al registrarse" }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _products.value = emptyList()
        _sales.value = emptyList()
        _suppliers.value = emptyList()
    }

    // --- Productos con soporte de Cloudinary ---
    fun createProductWithImage(product: Product, imageFile: File?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            var finalImageUrl = product.imageUri

            // Si se seleccionó un archivo local, lo subimos primero a Cloudinary
            if (imageFile != null) {
                val uploadedUrl = CloudinaryService.uploadImage(imageFile)
                if (uploadedUrl != null) {
                    finalImageUrl = uploadedUrl
                }
            }

            val productToSave = product.copy(imageUri = finalImageUrl)
            val success = repository.createProduct(productToSave)
            if (success) {
                refreshAllData()
                onSuccess()
            } else {
                errorMessage = "No se pudo guardar el producto"
            }
            isLoading = false
        }
    }

    fun toggleProduct(id: Int) {
        viewModelScope.launch {
            if (repository.toggleProduct(id)) {
                refreshAllData()
            }
        }
    }

    // --- Ventas ---
    fun createSale(sale: SaleItem, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            if (repository.createSale(sale)) {
                refreshAllData()
                onSuccess()
            } else {
                errorMessage = "Error al registrar la venta (verifique stock)"
            }
            isLoading = false
        }
    }

    // --- Proveedores ---
    fun createSupplier(supplier: Supplier, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            if (repository.createSupplier(supplier)) {
                refreshAllData()
                onSuccess()
            } else {
                errorMessage = "Error al registrar proveedor"
            }
            isLoading = false
        }
    }

    fun toggleSupplier(id: Int) {
        viewModelScope.launch {
            if (repository.toggleSupplier(id)) {
                refreshAllData()
            }
        }
    }

    // --- Preferencias ---
    fun updatePreferences(themeColor: String, isDarkMode: Boolean) {
        viewModelScope.launch {
            repository.updatePreferences(themeColor, isDarkMode)
        }
    }

    fun saveThemePreferences(themeColor: String, isDarkMode: Boolean) {
        updatePreferences(themeColor, isDarkMode)
    }
}