package com.vutka.vision.emoji.detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Environment
import android.support.annotation.DrawableRes
import android.util.Log
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.vutka.vision.emoji.utils.lazyFast
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.*
import java.util.Collections.rotate
import kotlin.reflect.KProperty
import android.R.attr.path
import com.vutka.vision.emoji.utils.createUniqueFileName


const val TAG = "BitmapGeneration"
const val ALBUM_NAME = "Vision Emoji"



class BitmapGeneration(
        private val context: Context,
        @DrawableRes private val drawableId: Int?,
        private val width: Int,
        private val height: Int,
        private val orientationFactor: Float = 1f
) {

    private val isPortrait: Boolean
        get() = height > width

    private val Bitmap.isPortrait: Boolean
        get() = height > width

    private var scaleFactor: Float = 1f

    private val imageFilePath: File? by lazyFast {

        File(context.filesDir, fileName)

    }

    val fileName: String = createUniqueFileName() + ".jpg"

    val info = Log.i(BitmapGeneration::class.java.simpleName,
            "resource id $drawableId width $width height $height orientation factor $orientationFactor filename $fileName")


    suspend fun convert(bytes: ByteArray) = async(CommonPool) {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                .rotateIfNecessary().let { newBitmap ->
                    saveImage(newBitmap)
                }
    }.await()

    private fun saveImage(newBitmap: Bitmap) {

        var outputStream: OutputStream? = null

        try {
            outputStream = FileOutputStream(imageFilePath)
            newBitmap.toJPG().also {
                outputStream.write(it, 0, it.size)
            }
        } catch (e: IOException) {
            Log.e(BitmapGeneration::class.java.name, "error writing image to public directory ", e)
        } finally {
            outputStream?.close()
        }


    }

    private fun Bitmap.toJPG(): ByteArray {

        return ByteArrayOutputStream().run {
            compress(Bitmap.CompressFormat.JPEG, 100, this)
            toByteArray()
        }
    }


    private fun Bitmap.rotateIfNecessary(): Bitmap = if (shouldRotate(this)) {
        rotateBitmap()
    } else {
        this
    }


    private fun shouldRotate(bitmap: Bitmap): Boolean = isPortrait != bitmap.isPortrait


    private fun Bitmap.rotateBitmap(): Bitmap = Bitmap.createBitmap(width, height, config).apply {
        Canvas(this).apply {
            rotate(90f * orientationFactor)
            matrix = Matrix().apply {
                if (orientationFactor > 0f)
                    postTranslate(0f, -this@rotateBitmap.height.toFloat())
                else
                    postTranslate(-this@rotateBitmap.width.toFloat(), 0f)
            }.also {
                drawBitmap(this@rotateBitmap, it, null)
            }
        }
    }


}








