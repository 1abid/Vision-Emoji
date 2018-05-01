package com.vutka.vision.emoji.detection

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import com.google.android.gms.vision.face.Face
import com.vutka.vision.emoji.R

class EmojiGraphic(private val overlay: EmojiOverlay,
                   private val resources: Resources = overlay.resources) : EmojiOverlay.Graphic(overlay) {


    private var drawable: Drawable? = null
    private var face: Face? = null


    var faceId: Int? = null

    @DrawableRes
    override var drawableResId: Int = 0
        set(value) {
                field = value
                drawable = ResourcesCompat.getDrawable(resources, value, overlay.context.theme)
        }

    fun updateFace(newFace: Face?) {
        face = newFace
        postInvalidate()
    }

    fun removeFace(){
        face = null
        drawable = null
        postInvalidate()
    }

    override fun draw(canvas: Canvas) {
        canvas.save().also {
            face?.also {
                drawable?.apply {
                    draw(canvas, it)
                }
            }
            canvas.restoreToCount(it)
        }
    }


    private fun Drawable.draw(canvas: Canvas, face: Face) {
        half(face.width, face.height) { halfWidth, halfHeight ->
            bounds.left = (translateX(face.position.x + halfWidth) - scaleX(halfWidth)).toInt()
            bounds.right = (translateX(face.position.x + halfWidth) + scaleX(halfWidth)).toInt()
            bounds.top = (translateY(face.position.y + halfHeight) - scaleY(halfHeight*-0.5F)).toInt() - face.height.toInt()
            bounds.bottom = (translateY(face.position.y + halfHeight) + scaleY(halfHeight*0.3F)).toInt() + halfHeight.toInt()
        }

        canvas.rotate(rotate(face.eulerZ), bounds.exactCenterX(), bounds.exactCenterY())
        draw(canvas)
    }

    private fun half(width: Float, height: Float, function: (halfWidth: Float, halfHeight: Float) -> Unit) {
        function(width / 2f, height / 2f)
    }

}