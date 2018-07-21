package com.vutka.vision.emoji.detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.vutka.vision.emoji.utils.lazyFast
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.*
import com.vutka.vision.emoji.utils.createUniqueFileName


private const val TAG = "BitmapGeneration"
private const val ALBUM_NAME = "Vision Emoji"


class BitmapGeneration(
        private val context: Context,
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

    private val emojify = Emojify()

    val info = Log.i(BitmapGeneration::class.java.simpleName,
            "width $width height $height orientation factor $orientationFactor filename $fileName")


    suspend fun convert(bytes: ByteArray) = async(CommonPool) {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                .rotateIfNecessary().let { newBitmap ->
                    newBitmap?.detectFace()?.let { faces ->
                        newBitmap?.setEmojiOverlay(faces)
                    } ?: newBitmap
                }.also {
                    saveImage(it)
                }
    }.await()


    private fun Bitmap.setEmojiOverlay(faces: SparseArray<Face>): Bitmap{
        return Bitmap.createBitmap(width, height, config).apply {
            Canvas(this).apply {
                drawBitmap(this@setEmojiOverlay, 0f, 0f, null)
                scale(1f / scaleFactor, 1f / scaleFactor)
                for (faceIndex in 0..faces.size()){
                    Log.i(TAG, "face index $faceIndex")
                }
            }
        }
    }

    private fun Drawable.draw(canvas: Canvas, face: Face) {
        bounds.left = (face.position.x).toInt()
        bounds.right = (face.position.x + face.width).toInt()
        bounds.top = (face.position.y).toInt() - (face.height / 4).toInt()
        bounds.bottom = (face.position.y + face.height).toInt() + (face.height.toInt() / 8)
        canvas.rotate(-face.eulerZ, bounds.exactCenterX(), bounds.exactCenterY())
        draw(canvas)
    }

    private fun Bitmap.detectFace(): SparseArray<Face>? =
            createFaceDetector().run {
                detect(Frame.Builder().setBitmap(scale()).build())?.apply {
                    release()
                }
            }

    private fun Bitmap.scale(): Bitmap? {
        scaleFactor = Math.min(1024 / Math.max(width, height).toFloat(), 1f)
        return if (scaleFactor < 1f) {
            Bitmap.createBitmap((width * scaleFactor).toInt(), (height * scaleFactor).toInt(), config).also {
                Canvas(it).apply {
                    scale(scaleFactor, scaleFactor)
                    drawBitmap(this@scale, 0f, 0f, null)
                }
            }
        } else {
            this
        }
    }


    private fun createFaceDetector(): FaceDetector =
            FaceDetector.Builder(context)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .build()


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








