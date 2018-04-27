package com.vutka.vision.emoji.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.*
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor
import com.vutka.vision.emoji.detection.FaceTracker
import com.vutka.vision.emoji.R
import com.vutka.vision.emoji.utils.CameraPersistance
import kotlinx.android.synthetic.main.fragment_scanner.*

/**
 * Created by abidhasanshaon on 11/3/18.
 */
class ScannerFragment : Fragment(),CameraPersistance.persistanceInstance {

    override var cameraState: CameraPersistance? = null

    private val RC_HANDLE_GSM = 9001

    private val TAG = ScannerFragment::class.java.simpleName

    private val googleApiAvailability = GoogleApiAvailability.getInstance()

    private var cameraSource: CameraSource? = null

    private var faceTracker: FaceTracker? = null

    private var detector: Detector<Face>? = null
        get() =
            if(field!=null) {
                field
            }
            else{
                if(emoji_overlay != null) {
                    faceTracker = FaceTracker(emoji_overlay)
                    field = FaceDetector.Builder(context)
                            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                            .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                            .build().apply {
                                setProcessor(MultiProcessor.Builder(GraphicFaceTrackerFactory(faceTracker)).build())
                            }
                }
                field
            }
        set(value){
            field?.apply {
                release()
            }
            field = value
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_scanner, container, false)


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_camera_preview, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.findItem(R.id.camera_facing)?.also {
            it.icon = ResourcesCompat.getDrawable(resources, getCameraFacingType(), context?.theme)
        }

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = item.run {

            when(itemId){

                R.id.camera_facing ->{
                    toggleCamera()
                    activity?.invalidateOptionsMenu()
                    true
                }

                R.id.emoji_refresh -> {
                    faceTracker?.removeGraphic()
                    activity?.invalidateOptionsMenu()
                    true
                }

                else -> true

            }

        }

    private fun toggleCamera() {
        cameraState?.apply {
            cameraSource?.also {
                cameraFacing = if(it.cameraFacing == CameraSource.CAMERA_FACING_BACK)
                    CameraSource.CAMERA_FACING_FRONT
                else
                    CameraSource.CAMERA_FACING_BACK

                createCameraSource(cameraFacing)
                startCameraSource()
            }
        }


    }


    private fun getCameraFacingType(): Int =
        if(cameraSource?.cameraFacing == CameraSource.CAMERA_FACING_FRONT)
            R.drawable.ic_camera_rear
        else
            R.drawable.ic_camera_front



    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        cameraState?.apply {
            createCameraSource(cameraFacing)
            startCameraSource()
        }

    }

    override fun onPause() {
        super.onPause()
        preview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        capture_photo?.setOnClickListener{
            (it as LottieAnimationView).playAnimation()
        }
    }

    private fun createCameraSource(cameraFacing: Int?) {
        stopCameraSource()
        detector = null

        if(detector?.isOperational == false){
            Log.w(TAG, "vision API is not yet available to detect face")
        }
        cameraSource = CameraSource.Builder(context , detector)
                .setRequestedPreviewSize(1024,820)
                .setRequestedFps(3f)
                .setFacing(cameraFacing!!)
                .build()


    }

    private fun startCameraSource() {
        checkGooglePlayService {
            createCameraSource(cameraSource?.cameraFacing)
            cameraSource?.also {
                preview.start(it,emoji_overlay)
                detectorCheck()
            }
        }
    }

    private fun detectorCheck() {
        if(detector?.isOperational == true){
            Log.d(TAG, "detector is operational")
        }else{
            Log.d(TAG, "detector is not functioning")
        }
    }

    private fun stopCameraSource() {
        cameraSource?.apply {
            stop()
            release()
            preview?.stop()
            preview?.releaseCameraSource()
            detector?.release()
            cameraSource = null
            faceTracker = null
            detector = null
        }
    }


    private fun checkGooglePlayService(function: () -> Unit) {
        googleApiAvailability.isGooglePlayServicesAvailable(context)?.also {
            when (it) {
                ConnectionResult.SUCCESS -> function()
                else -> googleApiAvailability.getErrorDialog(activity, it, RC_HANDLE_GSM)
            }
        }
    }

    private class GraphicFaceTrackerFactory(private val faceTracker : FaceTracker?) : MultiProcessor.Factory<Face>{
        override fun create(face: Face?): FaceTracker? = faceTracker

    }

}