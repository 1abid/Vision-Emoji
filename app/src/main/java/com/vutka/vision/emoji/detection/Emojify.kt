package com.vutka.vision.emoji.detection

import android.util.Log
import com.google.android.gms.vision.face.Face
import com.vutka.vision.emoji.R


private const val TAG = "Emojify"

class Emojify{

    private var emojiDrawableResId :Int? = null


    fun getEmojiDrawableId(face: Face?) : Int?{
        Log.d(FaceTracker.TAG, "face data ${face?.id} smiling probability ${face?.isSmilingProbability} " +
                "left eye open probability ${face?.isLeftEyeOpenProbability} right eye open probability ${face?.isRightEyeOpenProbability}")

        face?.also {
            if (it.isSmilingProbability >= 0.8 && it.isLeftEyeOpenProbability >= 0.8 && it.isRightEyeOpenProbability >= 0.8) {
                Log.i(TAG, "happy")
                emojiDrawableResId = R.drawable.ic_happy_normal
            }

            if (it.isSmilingProbability <= 0.1 && it.isLeftEyeOpenProbability >= 0.8 && it.isRightEyeOpenProbability >= 0.8) {
                Log.i(TAG, "SAD")
                emojiDrawableResId = R.drawable.ic_sad
            }

            if (it.isSmilingProbability == -1.0F && it.isLeftEyeOpenProbability >= 0.8 && it.isRightEyeOpenProbability >= 0.8) {
                Log.i(TAG, "shocked")
                emojiDrawableResId = R.drawable.ic_shocked
            }

            if (it.isLeftEyeOpenProbability >= 0.7 && it.isRightEyeOpenProbability <= 0.4) {
                Log.i(TAG, "right wink")
                emojiDrawableResId = R.drawable.ic_wink_right
            }

            if (it.isSmilingProbability <= 0.5 && it.isLeftEyeOpenProbability <= 0.4 && it.isRightEyeOpenProbability >= 0.7) {
                Log.i(TAG, "left wink")
                emojiDrawableResId = R.drawable.ic_wink_left
            }

            if (it.isLeftEyeOpenProbability <= 0.2f && it.isRightEyeOpenProbability <= 0.2f) {
                Log.i(TAG, "dead")
                emojiDrawableResId = R.drawable.ic_dead
            }
        }

        return emojiDrawableResId
    }

}