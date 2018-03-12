package com.vutka.vision.emoji


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor

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
                        .build()
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


    override fun onResume() {
        super.onResume()

        checkGooglePlayService {
            createCameraSource()
            cameraSource?.let {
                Log.d(TAG , "This will show as camera source surly not null")
            }
        }
    }

    private fun createCameraSource() {
        cameraSource = CameraSource.Builder(context , detector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640,480)
                .setRequestedFps(3f)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .build()

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