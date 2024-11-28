package br.com.ramires.gourment.coffeclient.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

class ImageCache {
    private val cache = mutableMapOf<String, Uri>()

    fun getCachedImage(context: Context, base64String: String): Uri? {
        return if (cache.containsKey(base64String)) {
            cache[base64String]
        } else {
            val tempUri = saveBase64ToTempFile(context, base64String)
            tempUri?.let {
                cache[base64String] = it
            }
            tempUri
        }
    }

    private fun saveBase64ToTempFile(context: Context, base64String: String): Uri? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val tempFile = File(context.cacheDir, "temp_image_${base64String.hashCode()}.jpg")
            FileOutputStream(tempFile).use { it.write(decodedBytes) }
            Uri.fromFile(tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
