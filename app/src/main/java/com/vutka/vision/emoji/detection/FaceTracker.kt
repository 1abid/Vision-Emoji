package com.vutka.vision.emoji.detection

import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face

/**
 * Created by abidhasanshaon on 12/3/18.
 */
class FaceTracker(private val emojiOverlay: EmojiOverlay,
                  private val emojiGraphic :EmojiGraphic = EmojiGraphic(emojiOverlay)) : Tracker<Face>() {


}