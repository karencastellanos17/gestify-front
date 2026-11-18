package com.example.inventario.data.repository

import android.content.Context
import com.example.inventario.data.model.CreateSupplierOrderRequest
import com.example.inventario.data.model.Expense
import com.example.inventario.data.model.Product
import com.example.inventario.data.model.Supplier
import com.example.inventario.data.model.SupplierOrder
import com.example.inventario.data.remote.ApiException
import com.example.inventario.data.remote.GestifyApi
import com.example.inventario.data.remote.NETWORK_ERROR_MESSAGE
import com.example.inventario.data.remote.PreferencesRequest
import com.example.inventario.data.remote.RetrofitClient
import com.example.inventario.data.remote.toApiException
import com.example.inventario.ui.theme.screens.SaleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class GestifyRepository(@Suppress("UNUSED_PARAMETER") context: Context) {
    private val api = RetrofitClient.retrofit.create(GestifyApi::class.java)

    private suspend fun <T> execute(block: suspend () -> Response<T>): Result<T?> =
        withContext(Dispatchers.IO) {
            try {
                val response = block()
                if (response.isSuccessful) {
                    Result.success(response.body())
                } else {
                    Result.failure(response.toApiException())
                }
            } catch (_: IOException) {
                Result.failure(ApiException(0, NETWORK_ERROR_MESSAGE))
            } catch (e: Exception) {
                Result.failure(ApiException(0, e.localizedMessage ?: "Error inesperado."))
            }
        }

    suspend fun getProducts(): Result<List<Product>> =
        execute { api.getProducts() }.map { it.orEmpty() }

    suspend fun createProduct(product: Product): Result<Unit> =
        execute { api.createProduct(product) }.map { }

    suspend fun updateProduct(product: Product): Result<Unit> =
        execute { api.updateProduct(product.id, product) }.map { }

    suspend fun toggleProduct(id: Int): Result<Unit> =
        execute { api.toggleProduct(id) }.map { }

    suspend fun getSales(): Result<List<SaleItem>> =
        execute { api.getSales() }.map { it.orEmpty() }

    suspend fun createSale(sale: SaleItem): Result<Unit> =
        execute { api.createSale(sale) }.map { }

    suspend fun getSuppliers(): Result<List<Supplier>> =
        execute { api.getSuppliers() }.map { it.orEmpty() }

    suspend fun createSupplier(supplier: Supplier): Result<Unit> =
        execute { api.createSupplier(supplier) }.map { }

    suspend fun updateSupplier(supplier: Supplier): Result<Unit> =
        execute { api.updateSupplier(supplier.id, supplier) }.map { }

    suspend fun toggleSupplier(id: Int): Result<Unit> =
        execute { api.toggleSupplier(id) }.map { }

    suspend fun getSupplierOrders(): Result<List<SupplierOrder>> =
        execute { api.getSupplierOrders() }.map { it.orEmpty() }

    suspend fun createSupplierOrder(request: CreateSupplierOrderRequest): Result<Unit> =
        execute { api.createSupplierOrder(request) }.map { }

    suspend fun receiveSupplierOrder(id: Int): Result<Unit> =
        execute { api.receiveSupplierOrder(id) }.map { }

    // --- MÉTODOS DE GASTOS EN EL REPOSITORIO ---
    suspend fun getExpenses(): Result<List<Expense>> =
        execute { api.getExpenses() }.map { it.orEmpty() }

    suspend fun createExpense(expense: Expense): Result<Unit> =
        execute { api.createExpense(expense) }.map { }

    suspend fun updatePreferences(themeColor: String, isDarkMode: Boolean): Result<Unit> =
        execute { api.updatePreferences(PreferencesRequest(themeColor, isDarkMode)) }.map { }
}