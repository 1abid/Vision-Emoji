package com.vutka.vision.emoji.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vutka.vision.emoji.R
import com.vutka.vision.emoji.detection.ALBUM_NAME
import com.vutka.vision.emoji.utils.lazyFast
import kotlinx.android.synthetic.main.fragment_preview.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.io.File


private const val TAG = "PreviewFragment"
private const val ARG_PATH = "imagePath"

class PreviewFragment : Fragment() {

    companion object {
        fun newInstance(filePath: String): PreviewFragment {
            Log.i(TAG, "file path: $filePath")
            val fragment = PreviewFragment()
            val args = Bundle()
            args.putString(ARG_PATH, filePath)
            fragment.arguments = args

            return fragment
        }
    }

    private var fileName: String? = null

    private val imageFilePath: File? by lazyFast {

        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ALBUM_NAME + fileName)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileName = this.arguments?.getString(ARG_PATH)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_preview, container, false)

    override fun onResume() {
        super.onResume()

        async(UI) {
            emoji_photo?.setImageBitmap(readFile())
        }

    }

    private suspend fun readFile(): Bitmap =
        async(CommonPool) {
            BitmapFactory.decodeFile(imageFilePath?.absolutePath)
        }.await()



}