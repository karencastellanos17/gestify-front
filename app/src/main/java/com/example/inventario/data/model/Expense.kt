package com.example.inventario.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Expense(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("idNegocio", alternate = ["id_negocio", "businessId"])
    val idNegocio: Long? = null,
    @SerializedName("concept")
    val concept: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("imageUri", alternate = ["imagenUrl", "imagen_url"])
    val imageUri: String? = null,
    @SerializedName("date")
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)