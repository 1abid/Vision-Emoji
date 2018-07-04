package com.vutka.vision.emoji.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import com.vutka.vision.emoji.*
import com.vutka.vision.emoji.R.id.toolbar
import com.vutka.vision.emoji.utils.CameraPersistance
import com.vutka.vision.emoji.utils.checkPermission
import com.vutka.vision.emoji.utils.isGrantedPermission
import com.vutka.vision.emoji.utils.lazyFast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ScannerFragment.EmojiFileConsumer {


    private val TAG = MainActivity::class.java.simpleName

    private val requestCode = 0
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var requiresPermission = false

    private val cameraPersistence: CameraPersistance by lazyFast {
        CameraPersistance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
        }

        checkPermission(*permissions) {
            requestPermission()
        }
    }


    private fun requestPermission() {
        requiresPermission = true
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    override fun onResume() {
        super.onResume()

        isGrantedPermission(*permissions) {
            if (requiresPermission) {
                requiresPermission = false
            }
            supportFragmentManager.createOrReturnFragment(ScannerFragment::class.java.canonicalName)
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.any { PackageManager.PERMISSION_DENIED == it }) {
            supportFragmentManager.createOrReturnFragment(MissingPermissionFragment::class.java.canonicalName)?.also {
                (it as MissingPermissionFragment).openSettings = goToSettings()
            }
        } else {
            requiresPermission = false
        }
    }

    private fun goToSettings(): () -> Unit = {
        with(Intent()) {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }


    private fun FragmentManager.createOrReturnFragment(fragmentClassName: String, addToBackStack: Boolean = false, arguments: String? = null, initialize: (fragment: Fragment) -> Unit = {}): Fragment {

        return when (fragmentClassName) {
            ScannerFragment::class.java.canonicalName -> createFragment(fragmentClassName, initialize, addToBackStack)
            PreviewFragment::class.java.canonicalName -> createFragment(fragmentClassName, initialize, addToBackStack, arguments)
            MissingPermissionFragment::class.java.canonicalName -> createFragment(fragmentClassName, initialize, addToBackStack)
            else -> findFragmentByTag(fragmentClassName)
        }
    }


    private fun createFragment(fragmentClassName: String, initialize: (fragment: Fragment) -> Unit, addToBackStack: Boolean, arguments: String? = null): Fragment {

        if (arguments != null) {
            return PreviewFragment.newInstance(arguments).also {
                supportFragmentManager.beginTransaction().apply {
                    if (addToBackStack)
                        addToBackStack(fragmentClassName)

                    replace(R.id.container, it, fragmentClassName)

                }.commit()
            }

        } else {
            return Fragment.instantiate(this, fragmentClassName).also {
                initialize(it)
                (it as? CameraPersistance.persistanceInstance)?.apply {
                    it.cameraState = this@MainActivity.cameraPersistence
                }
                supportFragmentManager.beginTransaction().apply {
                    if (addToBackStack)
                        addToBackStack(fragmentClassName)

                    replace(R.id.container, it, fragmentClassName)

                }.commit()
            }
        }
    }

    override fun setEmojiFileName(path: String) {
        supportFragmentManager.createOrReturnFragment(PreviewFragment::class.java.canonicalName, true, path)
    }
}
