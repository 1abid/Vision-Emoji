package com.vutka.vision.emoji.detection

import android.support.annotation.DrawableRes
import android.util.Log
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import java.util.*
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

    private val timer = Timer("enable_detection", true)

    private val emojify = Emojify()

    var drawableId: Int
        get() = emojiGraphic.drawableResId
        set(value) {
            emojiGraphic.drawableResId = value
        }

    override fun onNewItem(faceId: Int, face: Face?) {
        super.onNewItem(faceId, face)
        Log.i(TAG, "face found position ${face?.position} faceID $faceId")
        emojiGraphic.faceId = faceId

    }

    override fun onUpdate(detection: Detector.Detections<Face>, face: Face?) {
        if (detection.detectorIsOperational()) {
            if (enableEmoji) {
                drawableId = emojify.getEmojiDrawableId(face)!!
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
}