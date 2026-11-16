package com.example.inventario.data.remote


import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CloudinaryService {
    private const val CLOUD_NAME = "seg7lk8a"
    private const val UPLOAD_PRESET = "gestify_unsigned"
    private const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"

    suspend fun uploadImage(file: File): String? = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val responseBodyString = response.body?.string() ?: return@withContext null
                val jsonObject = JSONObject(responseBodyString)
                return@withContext jsonObject.optString("secure_url", null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}