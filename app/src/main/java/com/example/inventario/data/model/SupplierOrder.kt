package com.example.inventario.data.model

import com.google.gson.annotations.SerializedName

enum class PricingTier(val label: String, val multiplier: Int) {
    UNIDAD("Unidad (1)", 1),
    DECENA("Decena (10)", 10),
    DOCENA("Docena (12)", 12)
}

data class SupplierOrderItemDto(
    @SerializedName("productId")
    val productId: Int = 0,

    @SerializedName("productName")
    val productName: String = "",

    @SerializedName("pricingTier")
    val pricingTier: String = "UNIDAD",

    @SerializedName("packQuantity")
    val packQuantity: Int = 1,

    @SerializedName("totalUnits")
    val totalUnits: Int = 1,

    @SerializedName("tierPrice")
    val tierPrice: Double = 0.0,

    @SerializedName("realUnitCost")
    val realUnitCost: Double = 0.0,

    @SerializedName("subtotal")
    val subtotal: Double = 0.0
)

data class SupplierOrder(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("supplierId")
    val supplierId: Int = 0,

    @SerializedName("supplierName")
    val supplierName: String = "",

    @SerializedName("shippingCost")
    val shippingCost: Double = 0.0,

    @SerializedName("subtotalProducts")
    val subtotalProducts: Double = 0.0,

    @SerializedName("totalCost")
    val totalCost: Double = 0.0,

    @SerializedName("status")
    val status: String = "PENDIENTE",

    @SerializedName("orderDate")
    val orderDate: String = "",

    @SerializedName("receivedDate")
    val receivedDate: String? = null,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("items")
    val items: List<SupplierOrderItemDto> = emptyList()
)

data class CreateSupplierOrderItemRequest(
    val productId: Int,
    val pricingTier: String,
    val packQuantity: Int,
    val tierPrice: Double
)

data class CreateSupplierOrderRequest(
    val supplierId: Int,
    val shippingCost: Double,
    val notes: String? = null,
    val receiveImmediately: Boolean = false,
    val items: List<CreateSupplierOrderItemRequest>
)