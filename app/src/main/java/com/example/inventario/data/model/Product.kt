package com.example.inventario.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id", alternate = ["idProducto", "id_producto"])
    val id: Int = 0,

    @SerializedName("idNegocio", alternate = ["id_negocio", "businessId"])
    val idNegocio: Long? = null,

    @SerializedName("name", alternate = ["nombre"])
    val name: String,

    @SerializedName("category", alternate = ["categoria"])
    val category: String,

    @SerializedName("price", alternate = ["precio"])
    val price: Double,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("isActive", alternate = ["activo", "is_active"])
    val isActive: Boolean = true,

    @SerializedName("imageUri", alternate = ["imagenUrl", "imagen_url", "image_uri", "imagen"])
    val imageUri: String? = null
)