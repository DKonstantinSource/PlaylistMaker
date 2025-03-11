package com.example.playlistmaker

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object PhotoPickerUtil {

    fun copyImageToAppStorage(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val imageName = "cover_image_${System.currentTimeMillis()}.jpg"
        val appDirectory = File(context.filesDir, "cover_images")
        if (!appDirectory.exists()) {
            appDirectory.mkdir()
        }
        val imageFile = File(appDirectory, imageName)

        inputStream?.use {
            val outputStream = FileOutputStream(imageFile)
            it.copyTo(outputStream)
            outputStream.close()
        }
        return imageFile.absolutePath
    }
}
