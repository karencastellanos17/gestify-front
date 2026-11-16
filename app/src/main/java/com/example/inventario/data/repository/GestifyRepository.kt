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
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createProduct(product: Product): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.createProduct(businessId, product)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun toggleProduct(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            api.toggleProduct(id).isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // --- Ventas ---
    suspend fun getSales(): List<SaleItem> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSales(businessId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createSale(sale: SaleItem): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.createSale(businessId, sale)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // --- Proveedores ---
    suspend fun getSuppliers(): List<Supplier> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSuppliers(businessId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createSupplier(supplier: Supplier): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.createSupplier(businessId, supplier)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun toggleSupplier(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            api.toggleSupplier(id).isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // --- Preferencias ---
    suspend fun updatePreferences(themeColor: String, isDarkMode: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.updatePreferences(businessId, PreferencesRequest(themeColor, isDarkMode))
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}