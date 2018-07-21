package com.vutka.vision.emoji.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ShareActionProvider
import android.util.Log
import android.view.*
import com.vutka.vision.emoji.R
import com.vutka.vision.emoji.utils.lazyFast
import kotlinx.android.synthetic.main.fragment_preview.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.io.File


private const val TAG = "PreviewFragment"
private const val ARG_PATH = "imagePath"
private const val MYME_TYPE = "image/jpeg"

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

    private var bitmap : Bitmap? = null

    private val imageFilePath: File by lazyFast {

        File(context?.filesDir, fileName)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        fileName = this.arguments?.getString(ARG_PATH)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_preview, container, false)

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity).apply {
            supportActionBar?.apply {
                setHomeButtonEnabled(true)
                setDefaultDisplayHomeAsUpEnabled(true)
            }
        }

        async(UI) {
            emoji_photo?.setImageBitmap(readFile())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.photo_preview, menu).also {
            menu?.findItem(R.id.menu_share)?.apply {
                (MenuItemCompat.getActionProvider(this) as ShareActionProvider).also { actionProvider ->
                    context?.apply {
                        Intent(Intent.ACTION_SEND).also {
                            it.type = MYME_TYPE
                            it.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, packageName, imageFilePath))
                            it.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            actionProvider.setShareIntent(it)
                        }
                    }
                    actionProvider.setOnShareTargetSelectedListener { _,_ ->
                        false
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.itemId?.also {
            when (it) {
                android.R.id.home -> activity?.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun readFile():Bitmap =
            async(CommonPool) {
            BitmapFactory.decodeFile(imageFilePath?.absolutePath)

        }.await()



}