package me.rerere.rikkahub.codeinterpreter

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Matrix
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Base64

object ImageTools {
    fun inspect(file: File): Map<String, Any?> {
        require(file.isFile) { "Image not found: ${file.name}" }
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, opts)
        require(opts.outWidth > 0 && opts.outHeight > 0) { "Unsupported or invalid image" }
        return mapOf(
            "width" to opts.outWidth,
            "height" to opts.outHeight,
            "mime" to opts.outMimeType
        )
    }

    fun decodeBase64ToFile(base64: String, output: File): Map<String, Any?> {
        val raw = Base64.getDecoder().decode(base64)
        require(raw.size <= 10 * 1024 * 1024) { "Decoded image is too large" }
        output.parentFile?.mkdirs()
        output.writeBytes(raw)
        return inspect(output)
    }

    fun resize(
        input: File,
        output: File,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int = 90
    ): Map<String, Any?> {
        val bitmap = BitmapFactory.decodeFile(input.absolutePath)
            ?: error("Cannot decode image")
        val scale = minOf(
            maxWidth.toFloat() / bitmap.width,
            maxHeight.toFloat() / bitmap.height,
            1f
        )
        val w = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val h = (bitmap.height * scale).toInt().coerceAtLeast(1)
        val resized = Bitmap.createScaledBitmap(bitmap, w, h, true)
        output.parentFile?.mkdirs()
        output.outputStream().use { stream ->
            resized.compress(CompressFormat.JPEG, quality.coerceIn(1, 100), stream)
        }
        if (resized !== bitmap) resized.recycle()
        bitmap.recycle()
        return mapOf("width" to w, "height" to h, "path" to output.absolutePath)
    }
}
