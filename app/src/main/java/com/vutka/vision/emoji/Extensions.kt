package com.vutka.vision.emoji

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

/**
 * Created by abidhasanshaon on 7/3/18.
 */
fun Context.checkPermission(vararg permissions:String, function: () -> Unit){
    if(permissions.any { ContextCompat.checkSelfPermission(this , it) != PackageManager.PERMISSION_GRANTED }){
        function()
    }
}

fun Context.isGrantedPermission(vararg permissions: String , function: () -> Unit){
    if(permissions.any { ContextCompat.checkSelfPermission(this , it) == PackageManager.PERMISSION_GRANTED }){
        function()
    }
}