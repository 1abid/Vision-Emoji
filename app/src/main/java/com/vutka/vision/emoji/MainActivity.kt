package com.vutka.vision.emoji

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permission
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private val requestCode = 0
    private val permissions = arrayOf(Manifest.permission.CAMERA)
    private var requiresPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
        }

        checkPermission(*permissions){
            requestPermission()
        }
    }


    private fun requestPermission(){
        requiresPermission = true
        ActivityCompat.requestPermissions(this , permissions , requestCode)
    }

    override fun onResume() {
        super.onResume()

        isGrantedPermission(*permissions){
            if(requiresPermission){
                requiresPermission = false
            }

            supportFragmentManager.createOrReturnFragment(ScannerFragment::class.java.canonicalName)
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults.any { PackageManager.PERMISSION_GRANTED == it }){
            requiresPermission = false
            Log.i(TAG , "camera permission granted ")
        }else {
            supportFragmentManager.createOrReturnFragment(MissingPermissionFragment::class.java.canonicalName)?.also {
                (it as MissingPermissionFragment).openSettings = goToSettings()
            }
        }
    }

    private fun goToSettings(): () -> Unit = {
        with(Intent()){
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package" , packageName , null)
            startActivity(this)
        }
    }


    private fun FragmentManager.createOrReturnFragment(fragmentClassName: String , addToBackStack:Boolean = false , initialize: (fragment: Fragment) -> Unit ={}):Fragment
            = findFragmentByTag(fragmentClassName) ?: createFragment(fragmentClassName , initialize , addToBackStack)




    private fun createFragment(fragmentClassName : String , initialize : (fragment : Fragment) -> Unit , addToBackStack: Boolean) :Fragment =
            Fragment.instantiate(this , fragmentClassName).also {
                initialize(it)
                supportFragmentManager.beginTransaction().apply {
                    if(addToBackStack)
                        addToBackStack(fragmentClassName)

                    replace(R.id.container , it , fragmentClassName)

                }.commit()
            }

}
