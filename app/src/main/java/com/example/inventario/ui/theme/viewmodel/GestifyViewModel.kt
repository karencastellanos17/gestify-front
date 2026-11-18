package com.example.inventario.ui.theme.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventario.data.model.CreateSupplierOrderRequest
import com.example.inventario.data.model.Product
import com.example.inventario.data.model.Supplier
import com.example.inventario.data.model.SupplierOrder
import com.example.inventario.data.remote.CloudinaryService
import com.example.inventario.data.repository.AuthRepository
import com.example.inventario.data.repository.GestifyRepository
import com.example.inventario.ui.theme.screens.SaleItem
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class GestifyViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val repository = GestifyRepository(application)

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _sales = MutableStateFlow<List<SaleItem>>(emptyList())
    val sales: StateFlow<List<SaleItem>> = _sales.asStateFlow()

    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers.asStateFlow()

    private val _supplierOrders = MutableStateFlow<List<SupplierOrder>>(emptyList())
    val supplierOrders: StateFlow<List<SupplierOrder>> = _supplierOrders.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var syncError by mutableStateOf<String?>(null)
        private set

    init {
        if (authRepository.getBusinessId() != -1L) {
            refreshAllData()
        }
    }

    fun clearError() {
        errorMessage = null
    }

    fun clearSyncError() {
        syncError = null
    }

    fun refreshAllData() {
        viewModelScope.launch { loadAll() }
    }

    private suspend fun loadAll() {
        isLoading = true
        coroutineScope {
            val productsDeferred = async { repository.getProducts() }
            val salesDeferred = async { repository.getSales() }
            val suppliersDeferred = async { repository.getSuppliers() }
            val ordersDeferred = async { repository.getSupplierOrders() }

            val productsResult = productsDeferred.await()
            val salesResult = salesDeferred.await()
            val suppliersResult = suppliersDeferred.await()
            val ordersResult = ordersDeferred.await()

            productsResult.onSuccess { _products.value = it }
            salesResult.onSuccess { _sales.value = it }
            suppliersResult.onSuccess { _suppliers.value = it }
            ordersResult.onSuccess { _supplierOrders.value = it }

            val failure = listOf<Result<*>>(productsResult, salesResult, suppliersResult, ordersResult)
                .firstNotNullOfOrNull { it.exceptionOrNull() }
            syncError = failure?.let { "No se pudieron actualizar los datos: ${it.localizedMessage ?: "error desconocido"}" }
        }
        isLoading = false
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = authRepository.login(email, pass)
            result.fold(
                onSuccess = {
                    loadAll()
                    onSuccess()
                },
                onFailure = {
                    errorMessage = it.localizedMessage ?: "Error al iniciar sesión"
                    isLoading = false
                }
            )
        }
    }

    fun register(businessName: String, email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = authRepository.register(businessName, email, pass)
            result.fold(
                onSuccess = {
                    loadAll()
                    onSuccess()
                },
                onFailure = {
                    errorMessage = it.localizedMessage ?: "Error al registrarse"
                    isLoading = false
                }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _products.value = emptyList()
        _sales.value = emptyList()
        _suppliers.value = emptyList()
        _supplierOrders.value = emptyList()
        errorMessage = null
        syncError = null
        isLoading = false
    }

    fun createProductWithImage(product: Product, imageFile: File?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            var finalImageUrl = product.imageUri

            if (imageFile != null) {
                val uploadedUrl = CloudinaryService.uploadImage(imageFile)
                if (uploadedUrl == null) {
                    errorMessage = "No se pudo subir la imagen. Revisa tu conexión e intenta de nuevo."
                    isLoading = false
                    return@launch
                }
                finalImageUrl = uploadedUrl
            }

            val productToSave = product.copy(imageUri = finalImageUrl)
            val result = if (productToSave.id == 0) {
                repository.createProduct(productToSave)
            } else {
                repository.updateProduct(productToSave)
            }

            result.fold(
                onSuccess = {
                    loadAll()
                    onSuccess()
                },
                onFailure = {
                    errorMessage = it.localizedMessage ?: "No se pudo guardar el producto"
                }
            )
            isLoading = false
        }
    }

    fun toggleProduct(id: Int) {
        viewModelScope.launch {
            repository.toggleProduct(id).fold(
                onSuccess = { loadAll() },
                onFailure = { syncError = "No se pudo cambiar el estado del producto: ${it.localizedMessage}" }
            )
        }
    }

    fun createSale(sale: SaleItem, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            repository.createSale(sale).fold(
                onSuccess = {
                    loadAll()
                    onSuccess()
                },
                onFailure = {
                    errorMessage = it.localizedMessage ?: "Error al registrar la venta"
                }
            )
            isLoading = false
        }
    }

    fun createSupplier(supplier: Supplier, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = if (supplier.id == 0) {
                repository.createSupplier(supplier)
            } else {
                repository.updateSupplier(supplier)
            }
            result.fold(
                onSuccess = {
                    loadAll()
                    onSuccess()
                },
                onFailure = {
                    errorMessage = it.localizedMessage ?: "Error al guardar proveedor"
                }
            )
            isLoading = false
        }
    }

    fun toggleSupplier(id: Int) {
        viewModelScope.launch {
            repository.toggleSupplier(id).fold(
                onSuccess = { loadAll() },
                onFailure = { syncError = "No se pudo cambiar el estado del proveedor: ${it.localizedMessage}" }
            )
        }
    }

    fun createSupplierOrder(request: CreateSupplierOrderRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            repository.createSupplierOrder(request).fold(
                onSuccess = {
                    loadAll()
                    onSuccess()
                },
                onFailure = {
                    errorMessage = it.localizedMessage ?: "Error al registrar el pedido"
                }
            )
            isLoading = false
        }
    }

    fun receiveSupplierOrder(orderId: Int) {
        viewModelScope.launch {
            isLoading = true
            repository.receiveSupplierOrder(orderId).fold(
                onSuccess = { loadAll() },
                onFailure = { syncError = "No se pudo recibir el pedido: ${it.localizedMessage}" }
            )
            isLoading = false
        }
    }

    fun savedThemeColor(): String = authRepository.getThemeColor()

    fun savedDarkMode(): Boolean = authRepository.getDarkMode()

    fun saveThemePreferences(themeColor: String, isDarkMode: Boolean) {
        authRepository.saveTheme(themeColor, isDarkMode)
        viewModelScope.launch {
            repository.updatePreferences(themeColor, isDarkMode)
        }
    }
}