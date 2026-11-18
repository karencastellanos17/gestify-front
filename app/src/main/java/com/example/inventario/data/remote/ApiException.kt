package com.example.inventario.data.remote

import org.json.JSONObject
import retrofit2.Response

const val NETWORK_ERROR_MESSAGE =
    "No se pudo conectar con el servidor. Revisa tu internet. " +
            "Si el servidor estaba dormido, espera hasta un minuto e intenta de nuevo."

class ApiException(val code: Int, message: String) : Exception(message)

fun Response<*>.toApiException(): ApiException {
    val raw = try {
        errorBody()?.string().orEmpty()
    } catch (_: Exception) {
        ""
    }
    val serverMessage = try {
        JSONObject(raw).optString("error")
    } catch (_: Exception) {
        ""
    }
    val message = when {
        serverMessage.isNotBlank() -> serverMessage
        code() == 401 -> "Tu sesión expiró. Inicia sesión de nuevo."
        code() == 404 -> "No se encontró el recurso solicitado."
        code() in 500..599 -> "El servidor tuvo un problema. Intenta de nuevo en unos minutos."
        else -> "Error inesperado (${code()})."
    }
    return ApiException(code(), message)
}