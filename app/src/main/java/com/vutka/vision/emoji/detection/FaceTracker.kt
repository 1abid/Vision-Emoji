package com.vutka.vision.emoji.detection

import android.util.Log
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face

/**
 * Created by abidhasanshaon on 12/3/18.
 */
class FaceTracker(private val emojiOverlay: EmojiOverlay,
                  private val emojiGraphic: EmojiGraphic = EmojiGraphic(emojiOverlay)) : Tracker<Face>() {

    companion object {
        const val TAG = "faceTracker"
    }

    var enableEmoji: Boolean = true


    override fun onNewItem(faceId: Int, face: Face?) {
        super.onNewItem(faceId, face)
        Log.i(TAG, "face found ${face?.position}")
        //TODO set face id for each face found
    }

    override fun onUpdate(detection: Detector.Detections<Face>, face: Face?) {
        if (detection.detectorIsOperational()) {
            Log.i(TAG , "enable emoji $enableEmoji")
            if (enableEmoji)
                getAppropriateEmoji(face)
        }
    }

    override fun onMissing(detection: Detector.Detections<Face>) {

    }

    override fun onDone() {

    }

    private fun getAppropriateEmoji(face: Face?) {
        Log.d(TAG, "face data ${face?.id} smiling probability ${face?.isSmilingProbability} " +
                "left eye open probability ${face?.isLeftEyeOpenProbability} right eye open probability ${face?.isRightEyeOpenProbability}")
        face?.also {
            if (it.isSmilingProbability >= 0.9 && it.isLeftEyeOpenProbability >= 0.9 && it.isRightEyeOpenProbability >= 0.9) {
                Log.i(TAG, "this bro is straight happy")
                enableEmoji = false
            }
        }
    }
}