package com.example.inventario.data.model

import com.google.gson.annotations.SerializedName

data class Supplier(
    val id: Int = 0,

    @SerializedName("idNegocio", alternate = ["id_negocio", "businessId"])
    val idNegocio: Long? = null,

    @SerializedName("name")
    private val _name: String? = "",

    @SerializedName("contactName", alternate = ["contact_name"])
    private val _contactName: String? = "",

    @SerializedName("phone")
    private val _phone: String? = "",

    @SerializedName("category")
    private val _category: String? = "",

    @SerializedName("productIds", alternate = ["productosIds", "product_ids"])
    private val _productIds: List<Int>? = emptyList(),

    @SerializedName("extraItems", alternate = ["productosExtra", "extra_items"])
    private val _extraItems: List<String>? = emptyList(),

    val isActive: Boolean = true        // Soft Delete
) {
    val name: String
        get() = _name.orEmpty()

    val contactName: String
        get() = _contactName.orEmpty()

    val phone: String
        get() = _phone.orEmpty()

    val category: String
        get() = _category.orEmpty()

    val productIds: List<Int>
        get() = _productIds.orEmpty()

    val extraItems: List<String>
        get() = _extraItems.orEmpty()
}

/** Un proveedor distribuye un producto si está vinculado por id o si su nombre fue escrito a mano. */
fun Supplier.distributes(product: Product): Boolean =
    product.id in productIds ||
            extraItems.any { it.trim().equals(product.name.trim(), ignoreCase = true) }