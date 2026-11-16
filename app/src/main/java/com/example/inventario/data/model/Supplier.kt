package com.example.inventario.data.model

data class Supplier(
    val id: Int,
    val name: String,              // Nombre o empresa
    val contactName: String,       // Persona de contacto
    val phone: String,             // Solo informativo
    val category: String,          // Qué tipo de productos distribuye
    val productIds: List<Int> = emptyList(),    // Productos del inventario vinculados
    val extraItems: List<String> = emptyList(), // Productos escritos a mano
    val isActive: Boolean = true                // Soft Delete
)

/** Un proveedor distribuye un producto si está vinculado por id o si su nombre fue escrito a mano. */
fun Supplier.distributes(product: Product): Boolean =
    product.id in productIds ||
            extraItems.any { it.trim().equals(product.name.trim(), ignoreCase = true) }