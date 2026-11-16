package com.example.inventario.utils

object ValidationUtils {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val COLOMBIA_PHONE_REGEX = Regex("^(3\\d{9}|60[1-8]\\d{7})$")

    fun validateEmail(email: String): String? {
        val clean = email.trim()
        return when {
            clean.isEmpty() -> "El correo electrónico es obligatorio"
            clean.contains(" ") -> "El correo no puede contener espacios"
            !clean.contains("@") -> "Debe incluir el símbolo @"
            !clean.substringAfter("@").contains(".") -> "Debe terminar en un dominio válido (ej. .com, .co)"
            !EMAIL_REGEX.matches(clean) -> "Formato de correo inválido (ej. usuario@gmail.com)"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "La contraseña es obligatoria"
            password.length < 6 -> "Mínimo 6 caracteres"
            password.none { it.isDigit() } -> "Debe incluir al menos un número"
            else -> null
        }
    }

    fun validateColombiaPhone(phone: String): String? {
        val digits = phone.filter { it.isDigit() }
        return when {
            digits.isEmpty() -> "El número de teléfono es obligatorio"
            digits.length < 10 -> "Debe tener exactamente 10 dígitos (llevas ${digits.length})"
            digits.length > 10 -> "Máximo 10 dígitos para Colombia"
            !COLOMBIA_PHONE_REGEX.matches(digits) -> "Debe iniciar por 3 (celular) o 60 (fijo Colombia)"
            else -> null
        }
    }

    fun validatePersonOrCompanyName(name: String, label: String = "El nombre"): String? {
        val clean = name.trim()
        return when {
            clean.isEmpty() -> "$label es obligatorio"
            clean.length < 3 -> "$label debe tener al menos 3 caracteres"
            clean.length > 60 -> "$label no puede superar 60 caracteres"
            clean.all { it.isDigit() } -> "$label no puede ser solo números"
            else -> null
        }
    }

    fun validateContactName(name: String): String? {
        val clean = name.trim()
        if (clean.isEmpty()) return null
        return when {
            clean.length < 3 -> "El contacto debe tener al menos 3 letras"
            clean.any { it.isDigit() } -> "El nombre de contacto no debe tener números"
            else -> null
        }
    }

    fun validateProductName(name: String): String? {
        val clean = name.trim()
        return when {
            clean.isEmpty() -> "El nombre del producto es obligatorio"
            clean.length < 2 -> "Mínimo 2 caracteres"
            clean.length > 80 -> "Máximo 80 caracteres"
            else -> null
        }
    }

    fun validatePrice(priceText: String): String? {
        val value = priceText.toDoubleOrNull()
        return when {
            priceText.isBlank() -> "El precio es obligatorio"
            value == null -> "Ingresa un número válido"
            value <= 0 -> "El precio debe ser mayor a $0"
            value > 999_999_999 -> "El precio excede el límite permitido"
            else -> null
        }
    }

    fun validateStock(stockText: String): String? {
        val value = stockText.toIntOrNull()
        return when {
            stockText.isBlank() -> "El stock es obligatorio"
            value == null -> "Ingresa un número entero"
            value < 0 -> "El stock no puede ser negativo"
            value > 1_000_000 -> "El stock excede el límite permitido"
            else -> null
        }
    }
}