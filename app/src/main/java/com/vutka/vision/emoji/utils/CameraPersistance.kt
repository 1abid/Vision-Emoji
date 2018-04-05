package com.vutka.vision.emoji.utils

import android.content.Context
import com.google.android.gms.vision.CameraSource


class CameraPersistance(context: Context) {

    companion object {
        const val TAG = "CAMERA_STATE"
        const val CAMERA_FACING_KEY = "KEY_CAMERA_FACING"
    }

    var cameraFacing: Int by bindSharedPreference(context , CAMERA_FACING_KEY , CameraSource.CAMERA_FACING_FRONT)

    interface persistanceInstance{
        var cameraState: CameraPersistance?
    }

}