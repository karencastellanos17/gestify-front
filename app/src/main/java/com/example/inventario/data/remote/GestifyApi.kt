package com.example.inventario.data.remote


import com.example.inventario.data.model.Product
import com.example.inventario.data.model.Supplier
import com.example.inventario.ui.theme.screens.SaleItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

// ─── DTOs de Autenticación y Preferencias ───
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val businessName: String, val email: String, val password: String)

data class AuthResponse(
    val token: String,
    val idNegocio: Long,
    val businessName: String,
    val themeColor: String?,
    val isDarkMode: Boolean
)

data class PreferencesRequest(
    val themeColor: String,
    val isDarkMode: Boolean
)

interface GestifyApi {

    // ─── Auth ───
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // ─── Productos ───
    @GET("api/products")
    suspend fun getProducts(@Query("id_negocio") businessId: Long): Response<List<Product>>

    @POST("api/products")
    suspend fun createProduct(
        @Query("id_negocio") businessId: Long,
        @Body product: Product
    ): Response<Product>

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: Product
    ): Response<Product>

    @PATCH("api/products/{id}/toggle")
    suspend fun toggleProduct(@Path("id") id: Int): Response<Unit>

    // ─── Ventas ───
    @GET("api/sales")
    suspend fun getSales(@Query("id_negocio") businessId: Long): Response<List<SaleItem>>

    @POST("api/sales")
    suspend fun createSale(
        @Query("id_negocio") businessId: Long,
        @Body sale: SaleItem
    ): Response<SaleItem>

    // ─── Proveedores ───
    @GET("api/suppliers")
    suspend fun getSuppliers(@Query("id_negocio") businessId: Long): Response<List<Supplier>>

    @POST("api/suppliers")
    suspend fun createSupplier(
        @Query("id_negocio") businessId: Long,
        @Body supplier: Supplier
    ): Response<Supplier>

    @PUT("api/suppliers/{id}")
    suspend fun updateSupplier(
        @Path("id") id: Int,
        @Body supplier: Supplier
    ): Response<Supplier>

    @PATCH("api/suppliers/{id}/toggle")
    suspend fun toggleSupplier(@Path("id") id: Int): Response<Unit>

    // ─── Preferencias del Negocio ───
    @PUT("api/businesses/{id}/preferences")
    suspend fun updatePreferences(
        @Path("id") businessId: Long,
        @Body preferences: PreferencesRequest
    ): Response<Unit>
}