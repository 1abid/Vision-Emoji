package com.vutka.vision.emoji


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor
import kotlinx.android.synthetic.main.fragment_scanner.*

/**
 * Created by abidhasanshaon on 11/3/18.
 */
class ScannerFragment : Fragment() {

    private val RC_HANDLE_GSM = 9001

    private val TAG = ScannerFragment::class.java.simpleName

    private val googleApiAvailability = GoogleApiAvailability.getInstance()

    private var cameraSource: CameraSource? = null

    private var detector: Detector<Face>? = null
        get() =
            if(field!=null) {
                field
            }
            else{
                field = FaceDetector.Builder(context)
                        .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .build().apply {
                            setProcessor(LargestFaceFocusingProcessor(this, FaceTracker()))
                        }
                field
            }
        set(value){
            field?.apply {
                release()
            }

            field = value
        }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_scanner, container, false)


    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        createCameraSource()
        startCameraSource()
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun createCameraSource() {
        stopCameraSource()
        detector = null
        cameraSource = CameraSource.Builder(context , detector)
                .setRequestedPreviewSize(640,480)
                .setRequestedFps(3f)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .build()


    }

    private fun startCameraSource() {
        checkGooglePlayService {
            createCameraSource()
            cameraSource?.also {
                preview.start(it)
            }
        }
    }

    private fun stopCameraSource() {
        cameraSource?.apply {
            stop()
            release()
            detector?.release()
            cameraSource = null
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

}