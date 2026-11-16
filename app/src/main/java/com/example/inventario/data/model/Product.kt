package com.example.inventario.data.model


data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val isActive: Boolean = true,   // <--- comma added
    val imageUri: String? = null
)