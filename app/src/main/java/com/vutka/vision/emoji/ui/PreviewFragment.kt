package com.vutka.vision.emoji.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vutka.vision.emoji.R


private const val TAG = "PreviewFragment"
private const val ARG_PATH = "imagePath"

class PreviewFragment : Fragment() {

    companion object {
        fun newInstance(filePath: String):PreviewFragment {
            Log.i(TAG, "file path: $filePath")
            val fragment = PreviewFragment()
            val args = Bundle()
            args.putString(ARG_PATH, filePath)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "file path ${this.arguments?.get(ARG_PATH)}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_preview, container, false)


}