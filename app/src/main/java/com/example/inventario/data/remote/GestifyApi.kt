package com.example.inventario.data.remote

import com.example.inventario.data.model.Product
import com.example.inventario.data.model.Supplier
import com.example.inventario.ui.theme.screens.SaleItem
import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val businessName: String, val email: String, val password: String)

data class AuthResponse(
    val token: String,
    val idNegocio: Long,
    val businessName: String,
    val email: String? = null,
    val themeColor: String?,
    val isDarkMode: Boolean
)

data class PreferencesRequest(
    val themeColor: String,
    val isDarkMode: Boolean
)

interface GestifyApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>

    @POST("api/products")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: Product
    ): Response<Product>

    @PATCH("api/products/{id}/toggle")
    suspend fun toggleProduct(@Path("id") id: Int): Response<Unit>

    @GET("api/sales")
    suspend fun getSales(): Response<List<SaleItem>>

    @POST("api/sales")
    suspend fun createSale(@Body sale: SaleItem): Response<SaleItem>

    @GET("api/suppliers")
    suspend fun getSuppliers(): Response<List<Supplier>>

    @POST("api/suppliers")
    suspend fun createSupplier(@Body supplier: Supplier): Response<Supplier>

    @PUT("api/suppliers/{id}")
    suspend fun updateSupplier(
        @Path("id") id: Int,
        @Body supplier: Supplier
    ): Response<Supplier>

    @PATCH("api/suppliers/{id}/toggle")
    suspend fun toggleSupplier(@Path("id") id: Int): Response<Unit>

    @PUT("api/businesses/me/preferences")
    suspend fun updatePreferences(@Body preferences: PreferencesRequest): Response<Unit>
}