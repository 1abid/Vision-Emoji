package com.vutka.vision.emoji.detection

import android.util.Log
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.vutka.vision.emoji.R

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
        emojiGraphic.faceId = face?.id
    }

    override fun onUpdate(detection: Detector.Detections<Face>, face: Face?) {
        if (detection.detectorIsOperational()) {
            if(enableEmoji)
                getAppropriateEmoji(face)

            emojiOverlay.add(emojiGraphic)
            emojiGraphic.updateFace(face)
        }
    }

    override fun onMissing(detection: Detector.Detections<Face>) {
        emojiOverlay.remove(emojiGraphic)
        enableEmoji = true
    }

    override fun onDone() {
        emojiOverlay.remove(emojiGraphic)
        enableEmoji = true
    }

    fun removeGraphic(){
        emojiOverlay.remove(emojiGraphic)
        emojiGraphic.removeFace()
        Log.d(TAG,"graphic removed")
        enableEmoji = true
    }

    private fun getAppropriateEmoji(face: Face?) {
        Log.d(TAG, "face data ${face?.id} smiling probability ${face?.isSmilingProbability} " +
                "left eye open probability ${face?.isLeftEyeOpenProbability} right eye open probability ${face?.isRightEyeOpenProbability}")

        face?.also {
            if (it.isSmilingProbability >= 0.8 && it.isLeftEyeOpenProbability >= 0.8 && it.isRightEyeOpenProbability >= 0.8) {
                Log.i(TAG, "this bro is straight happy")
                emojiGraphic.drawableResId = R.drawable.ic_happy_normal
                enableEmoji = false
            }
        }
    }
}