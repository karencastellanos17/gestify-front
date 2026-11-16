package com.example.inventario.data.repository

import android.content.Context
import com.example.inventario.data.model.Product
import com.example.inventario.data.model.Supplier
import com.example.inventario.data.remote.GestifyApi
import com.example.inventario.data.remote.PreferencesRequest
import com.example.inventario.data.remote.RetrofitClient
import com.example.inventario.data.remote.TokenManager
import com.example.inventario.ui.theme.screens.SaleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GestifyRepository(context: Context) {
    private val api = RetrofitClient.retrofit.create(GestifyApi::class.java)
    private val tokenManager = TokenManager(context)
    private val businessId: Long
        get() = tokenManager.getBusinessId()

    // --- Productos ---
    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProducts(businessId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun createProduct(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (businessId == -1L) {
                return@withContext Result.failure(Exception("Sesión inválida (id_negocio = -1). Cierra sesión e ingresa de nuevo."))
            }
            val payload = product.copy(idNegocio = businessId)
            val response = api.createProduct(businessId, payload)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errBody = response.errorBody()?.string().orEmpty()
                Result.failure(Exception("HTTP ${response.code()}: ${errBody.ifBlank { response.message() }}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.localizedMessage}"))
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val payload = product.copy(idNegocio = businessId)
            val response = api.updateProduct(product.id, payload)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errBody = response.errorBody()?.string().orEmpty()
                Result.failure(Exception("HTTP ${response.code()}: ${errBody.ifBlank { response.message() }}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.localizedMessage}"))
        }
    }

    suspend fun toggleProduct(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            api.toggleProduct(id).isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    // --- Ventas ---
    suspend fun getSales(): List<SaleItem> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSales(businessId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun createSale(sale: SaleItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (businessId == -1L) {
                return@withContext Result.failure(Exception("Sesión inválida (id_negocio = -1). Cierra sesión e ingresa de nuevo."))
            }
            val payload = sale.copy(idNegocio = businessId)
            val response = api.createSale(businessId, payload)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errBody = response.errorBody()?.string().orEmpty()
                Result.failure(Exception("HTTP ${response.code()}: ${errBody.ifBlank { response.message() }}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.localizedMessage}"))
        }
    }

    // --- Proveedores ---
    suspend fun getSuppliers(): List<Supplier> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSuppliers(businessId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun createSupplier(supplier: Supplier): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (businessId == -1L) {
                return@withContext Result.failure(Exception("Sesión inválida (id_negocio = -1). Cierra sesión e ingresa de nuevo."))
            }
            val payload = supplier.copy(idNegocio = businessId)
            val response = api.createSupplier(businessId, payload)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errBody = response.errorBody()?.string().orEmpty()
                Result.failure(Exception("HTTP ${response.code()}: ${errBody.ifBlank { response.message() }}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.localizedMessage}"))
        }
    }

    suspend fun updateSupplier(supplier: Supplier): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val payload = supplier.copy(idNegocio = businessId)
            val response = api.updateSupplier(supplier.id, payload)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errBody = response.errorBody()?.string().orEmpty()
                Result.failure(Exception("HTTP ${response.code()}: ${errBody.ifBlank { response.message() }}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.localizedMessage}"))
        }
    }

    suspend fun toggleSupplier(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            api.toggleSupplier(id).isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    // --- Preferencias ---
    suspend fun updatePreferences(themeColor: String, isDarkMode: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.updatePreferences(businessId, PreferencesRequest(themeColor, isDarkMode))
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }
}