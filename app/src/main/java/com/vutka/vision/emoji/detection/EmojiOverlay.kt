package com.vutka.vision.emoji.detection

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.android.gms.vision.CameraSource

class EmojiOverlay(context: Context, attributeSet: AttributeSet): View(context, attributeSet){

    private val mLock = Any()
    private var mPreviewWidth: Int = 0
    private var mWidthScaleFactor: Float = 1.0f
    private var mPreviewHeight: Int = 0
    private var mHeightScaleFactor: Float = 1.0f
    private var mCameraFacing = CameraSource.CAMERA_FACING_BACK
    private val mGraphic = HashSet<Graphic>()


    abstract class Graphic(private val mEmojiOverlay: EmojiOverlay){

        abstract fun draw(canvas: Canvas)

        fun scaleX(horizontal: Float) = horizontal * mEmojiOverlay.mWidthScaleFactor

        fun scaleY(vertical: Float) = vertical * mEmojiOverlay.mHeightScaleFactor

        fun TransletX(x: Float) :Float = if (mEmojiOverlay.mCameraFacing== CameraSource.CAMERA_FACING_FRONT){
            mEmojiOverlay.width - scaleX(x)
        }else{
            scaleX(x)
        }

        fun TransletY(y: Float) :Float = scaleY(y)

        fun postInvalidate() = mEmojiOverlay.postInvalidate()

    }


    public fun clear(){
        synchronized(mLock){
            mGraphic.clear()
        }
        postInvalidate()
    }

    public fun add(emoji: Graphic){
        synchronized(mLock){
            mGraphic.add(emoji)
        }
        postInvalidate()
    }

    public fun setCameraInfo(previewWidth:Int, previewHeight:Int, camreFacing:Int){
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