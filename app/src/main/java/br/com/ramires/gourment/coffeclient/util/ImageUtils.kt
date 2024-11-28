package br.com.ramires.gourment.coffeclient.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    // Converte a imagem do URI para uma string Base64
    fun convertImageToBase64(context: Context, imageUri: Uri): String? {
        return try {
            // Abre o InputStream do URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Fecha o InputStream após leitura
            inputStream?.close()

            // Reduz o tamanho da imagem
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true)

            // Usa um ByteArrayOutputStream para compactar o bitmap
            val outputStream = ByteArrayOutputStream()

            // Compacta o bitmap para JPEG (para reduzir o tamanho do arquivo com compressão)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

            // Converte o stream para um array de bytes e, então, para Base64
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
