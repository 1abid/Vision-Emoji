package com.vutka.vision.emoji

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup

/**
 * Created by abidhasanshaon on 12/3/18.
 */
class CameraSourcePreview(context : Context, attributeSet: AttributeSet) : ViewGroup(context , attributeSet) {

     companion object {
        val logTAG = CameraSourcePreview::class.java.simpleName!!
    }

    private var surfaceView : SurfaceView


    init {
        Log.i(logTAG , " class initialization ")

        surfaceView = SurfaceView(context)
        surfaceView.holder.addCallback(SurfaceViewHolderCallback())
        addView(surfaceView)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d(logTAG , "onLayout called with left:$l , top:$t , right:$r , bottom:$b")
        /*surfaceView?.let {
            Log.i(logTAG , "surfaceView ${it.holder.isCreating}")
        }*/
    }



    private class SurfaceViewHolderCallback : SurfaceHolder.Callback{

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            Log.i(logTAG , "surfaceView changed ")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            Log.i(logTAG , "surfaceView destroyed ")
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            Log.i(logTAG , "surfaceView created ")
        }

    }
}