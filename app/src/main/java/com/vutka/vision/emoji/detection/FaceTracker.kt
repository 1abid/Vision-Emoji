package com.vutka.vision.emoji.detection

import android.support.annotation.DrawableRes
import android.util.Log
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.vutka.vision.emoji.R
import java.util.*
import java.util.logging.Handler
import kotlin.concurrent.schedule

/**
 * Created by abidhasanshaon on 12/3/18.
 */
class FaceTracker(private val emojiOverlay: EmojiOverlay,
                  private val emojiGraphic: EmojiGraphic = EmojiGraphic(emojiOverlay)) : Tracker<Face>() {

    companion object {
        const val TAG = "faceTracker"
    }

    var enableEmoji: Boolean = true

    @DrawableRes
    var drawableID: Int = 0

    private val timer = Timer("enable_detection", true)

    override fun onNewItem(faceId: Int, face: Face?) {
        super.onNewItem(faceId, face)
        Log.i(TAG, "face found position ${face?.position} faceID $faceId")
        emojiGraphic.faceId = faceId
    }

    override fun onUpdate(detection: Detector.Detections<Face>, face: Face?) {
        if (detection.detectorIsOperational()) {
            if (enableEmoji) {
                getAppropriateEmoji(face)
                emojiOverlay.add(emojiGraphic)
                emojiGraphic.updateFace(face)
            }

        }
    }

    override fun onMissing(detection: Detector.Detections<Face>) {
        emojiOverlay.remove(emojiGraphic)
    }

    override fun onDone() {
        emojiOverlay.remove(emojiGraphic)
    }

    fun removeGraphic() {
        emojiOverlay.remove(emojiGraphic)
        emojiGraphic.removeFace()
        Log.d(TAG, "graphic removed")
        enableEmoji = false
        
        timer.schedule(3000) {
            enableEmoji = true
        }

    }

    private fun getAppropriateEmoji(face: Face?) {
        Log.d(TAG, "face data ${face?.id} smiling probability ${face?.isSmilingProbability} " +
                "left eye open probability ${face?.isLeftEyeOpenProbability} right eye open probability ${face?.isRightEyeOpenProbability}")

        face?.also {
            if (it.isSmilingProbability >= 0.8 && it.isLeftEyeOpenProbability >= 0.8 && it.isRightEyeOpenProbability >= 0.8) {
                Log.i(TAG, "happy")
                emojiGraphic.drawableResId = R.drawable.ic_happy_normal
                drawableID = R.drawable.ic_happy_normal
            }

            if (it.isSmilingProbability <= 0.1 && it.isLeftEyeOpenProbability >= 0.8 && it.isRightEyeOpenProbability >= 0.8) {
                Log.i(TAG, "SAD")
                emojiGraphic.drawableResId = R.drawable.ic_sad
                drawableID = R.drawable.ic_sad
            }

            if (it.isSmilingProbability == -1.0F && it.isLeftEyeOpenProbability >= 0.8 && it.isRightEyeOpenProbability >= 0.8) {
                Log.i(TAG, "shocked")
                emojiGraphic.drawableResId = R.drawable.ic_shocked
                drawableID = R.drawable.ic_shocked
            }

            if (it.isLeftEyeOpenProbability >= 0.7 && it.isRightEyeOpenProbability <= 0.4) {
                Log.i(TAG, "right wink")
                emojiGraphic.drawableResId = R.drawable.ic_wink_right
                drawableID = R.drawable.ic_wink_right
            }

            if (it.isSmilingProbability <= 0.5 && it.isLeftEyeOpenProbability <= 0.4 && it.isRightEyeOpenProbability >= 0.7) {
                Log.i(TAG, "left wink")
                emojiGraphic.drawableResId = R.drawable.ic_wink_left
                drawableID = R.drawable.ic_wink_left
            }

            if (it.isLeftEyeOpenProbability <= 0.2f && it.isRightEyeOpenProbability <= 0.2f) {
                Log.i(TAG, "dead")
                emojiGraphic.drawableResId = R.drawable.ic_dead
                drawableID = R.drawable.ic_dead
            }
        }
    }
}