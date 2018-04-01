package com.vutka.vision.emoji

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.google.android.gms.vision.CameraSource

/**
 * Created by abidhasanshaon on 12/3/18.
 */
class CameraSourcePreview(context : Context, attributeSet: AttributeSet) : ViewGroup(context , attributeSet) {

     companion object {
        val logTAG = CameraSourcePreview::class.java.simpleName!!
    }

    private var surfaceView : SurfaceView
    private var cameraSource: CameraSource? = null
    set(value) {
        field = value
        startPreviewIfReady()
    }



    init {
        Log.i(logTAG , " class initialization ")

        surfaceView = SurfaceView(context)
        surfaceView.holder.addCallback(SurfaceViewHolderCallback())
        addView(surfaceView)

    }

    fun start(cameraSource: CameraSource){
        this.cameraSource = cameraSource
    }

    private fun startPreviewIfReady() {
        cameraSource?.let {
            Log.i(logTAG , "CameraSource preview  width ${it?.previewSize?.width} ${it?.previewSize?.width}")
        }
    }



    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d(logTAG , "onLayout called with left:$l , top:$t , right:$r , bottom:$b")
        previewSize{preViewWidth , preViewHeight ->
            viewWidthHeight { viewWidth, viewHeight ->
                Log.i(logTAG , "preview width $preViewWidth previewHeight $preViewHeight view width $viewWidth viewHeight $viewHeight")
                viewRation(preViewWidth , preViewHeight , viewWidth , viewHeight) { viewWidthRation, viewHeightRation ->
                    Log.i(logTAG , "view width ratio $viewWidthRation height ratio $viewHeightRation")
                    layoutChildrenMeasurement(preViewWidth , preViewHeight , viewWidth , viewHeight , viewWidthRation , viewHeightRation)

                }
            }
        }
    }


    private fun viewWidthHeight(function: (viewWidthRation: Int, viewHeightRation: Int) -> Unit) =
            function(right - left , bottom - top)

    private fun viewRation(previewWidth: Int ,
                           preViewHeight: Int ,
                           viewWidth: Int ,
                           viewHeight: Int ,
                           function :(viewHeightRation:Float , viewWidthRation:Float) ->Unit) =
            function(viewHeight.toFloat() / preViewHeight.toFloat() , viewWidth.toFloat() / previewWidth.toFloat())

    private fun previewSize(function:(previewWidth:Int , preViewHeight:Int) -> Unit){
        configureOrientationRotation(cameraSource?.previewSize?.width ?: 480,cameraSource?.previewSize?.height ?: 640,function)
    }

    private fun configureOrientationRotation(width: Int, height: Int, function: (previewWidth: Int, preViewHeight: Int) -> Unit){
        if(isPortrait)
            function(width , height)
        else
            function(height , width)
    }

    private fun layoutChildrenMeasurement(previewWidth: Int ,
                                          preViewHeight: Int ,
                                          viewWidth: Int ,
                                          viewHeight: Int ,
                                          viewWidthRation: Float ,
                                          viewHeightRation: Float){
        if(viewWidthRation>viewHeightRation){
            (preViewHeight.toFloat() * viewWidthRation).toInt().also {childViewHeight ->
                Log.i(logTAG , "after calculation for aspect ratio childViewHeight $childViewHeight")

                layoutChildren(0 , (childViewHeight - viewHeight)/2 , viewWidth , childViewHeight)
            }
        }else{
            (previewWidth.toFloat() * viewHeightRation).toInt().also {childViewWidth ->
                Log.i(logTAG , "after calculation for aspect ratio childViewWidth $childViewWidth")

                layoutChildren((childViewWidth - viewWidth) / 2, 0, childViewWidth, viewHeight)
            }
        }

    }

    private fun layoutChildren(childXOffset:Int, childYOffset:Int, childViewWidth:Int, childViewHeight:Int){
        forEachChildren {
            Log.i(logTAG , "childXOffset $childXOffset childYOffset " +
                    "$childYOffset childViewWidth $childViewWidth childViewHeight $childViewHeight")
            it.layout(-childXOffset, -childYOffset, childViewWidth - childXOffset, childViewHeight - childYOffset)
        }
    }


    private val isPortrait:Boolean = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

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