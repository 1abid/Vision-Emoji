package com.vutka.vision.emoji.detection

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.android.gms.vision.CameraSource

class EmojiOverlay(context: Context, attributeSet: AttributeSet): View(context, attributeSet){

    private val mLock = Any()
    private var mPreviewWidth: Int = 0
    private var mWidthScaleFactor: Float = 1.0f
    private var mPreviewHeight: Int = 0
    private var mHeightScaleFactor: Float = 1.0f
    private var mCameraFacing = CameraSource.CAMERA_FACING_FRONT
    private val mGraphic = HashSet<Graphic>()


    abstract class Graphic(private val mEmojiOverlay: EmojiOverlay){

        abstract var drawableResId :Int

        abstract fun draw(canvas: Canvas)

        fun scaleX(horizontal: Float) = horizontal * mEmojiOverlay.mWidthScaleFactor

        fun scaleY(vertical: Float) = vertical * mEmojiOverlay.mHeightScaleFactor

        fun translateX(x: Float) :Float = if (mEmojiOverlay.mCameraFacing == CameraSource.CAMERA_FACING_FRONT){
            mEmojiOverlay.width - scaleX(x)
        }else{
            scaleX(x)
        }

        fun translateY(y: Float) :Float = scaleY(y)

        fun rotate(angle : Float):Float =
            if(mEmojiOverlay.mCameraFacing == CameraSource.CAMERA_FACING_FRONT){
                angle
            }else{
                - angle
            }


        fun postInvalidate() = mEmojiOverlay.postInvalidate()

    }


    fun clear(){
        synchronized(mLock){
            mGraphic.clear()
        }
        postInvalidate()
    }

    fun add(emoji: Graphic){
        synchronized(mLock){
            mGraphic.add(emoji)
        }
        postInvalidate()
    }

    fun remove(emoji: Graphic){
        synchronized(mLock){
            mGraphic.remove(emoji)
        }
        postInvalidate()
    }

    fun setCameraInfo(previewWidth:Int, previewHeight:Int, camreFacing:Int){
        synchronized(mLock){
            mPreviewWidth = previewWidth
            mPreviewHeight = previewHeight
            mCameraFacing = camreFacing
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(mPreviewWidth !=0 && mPreviewHeight !=0){
            synchronized(mLock){
                mWidthScaleFactor = canvas.width.toFloat() / mPreviewWidth.toFloat()
                mHeightScaleFactor = canvas.height.toFloat() / mPreviewHeight.toFloat()
            }

            for (graphic in mGraphic){
                graphic.draw(canvas)
            }
        }
    }


}