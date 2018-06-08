package com.vutka.vision.emoji.detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.support.annotation.DrawableRes
import android.util.Log
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.util.Collections.rotate

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

    val info = Log.i(BitmapGeneration::class.java.simpleName,
            "resource id $drawableId width $width height $height orientation factor $orientationFactor")


    suspend fun convert(bytes: ByteArray) = async(CommonPool) {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                .rotateIfNecessary().let { newBitmap->
                    newBitmap.detectFace()?.let{

                    }
                }
    }


    private fun Bitmap.detectFace(): Face? =
        createFaceDetector().run {
            detect(Frame.Builder().setBitmap(scaleBitmap()).build())?.first().apply {
                release()
            }
        }


    private fun createFaceDetector() :FaceDetector =
            FaceDetector.Builder(context)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS).build()

    private fun Bitmap.scaleBitmap() :Bitmap{
        scaleFactor = Math.min(1080f / Math.max(width, height).toFloat(), 1f)
        return if (scaleFactor < 1f) {
            Bitmap.createBitmap((width * scaleFactor).toInt(), (height * scaleFactor).toInt(), config).also {
                Canvas(it).apply {
                    scale(scaleFactor, scaleFactor)
                    drawBitmap(this@scaleBitmap, 0f, 0f, null)
                }
            }
        } else {
            this
        }
    }

    private fun Bitmap.rotateIfNecessary() : Bitmap = if (shouldRotate(this)) {
        rotateBitmap()
    } else {
        this
    }


    private fun shouldRotate(bitmap: Bitmap): Boolean = isPortrait != bitmap.isPortrait


    private fun Bitmap.rotateBitmap(): Bitmap = Bitmap.createBitmap(width, height, config).apply {
        Canvas(this).apply {
            rotate(90f * orientationFactor)
            matrix = Matrix().apply {
                if(orientationFactor > 0f)
                    postTranslate(0f, -this@rotateBitmap.height.toFloat())
                else
                    postTranslate(-this@rotateBitmap.width.toFloat() , 0f)
            }.also {
                drawBitmap(this@rotateBitmap, it, null)
            }
        }
    }


    private fun <T> SparseArray<T>.first(): T? =
            takeIf { it.size() > 0 }?.get(keyAt(0))

}








