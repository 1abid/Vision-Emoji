package com.vutka.vision.emoji.detection

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.DrawableRes
import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

class BitmapGeneration(
        private val context: Context,
        @DrawableRes private val drawableId: Int?,
        private val width:Int,
        private val height:Int,
        private val orientationFactor: Float = 1f
) {

    val info = Log.i(BitmapGeneration::class.java.simpleName,
            "resource id $drawableId width $width height $height orientation factor $orientationFactor")


    suspend fun convert(butes: ByteArray)  = async(CommonPool) {

    }

}