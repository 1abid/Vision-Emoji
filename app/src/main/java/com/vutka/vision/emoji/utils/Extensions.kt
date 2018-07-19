package com.vutka.vision.emoji.utils

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors.callable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Created by abidhasanshaon on 7/3/18.
 */
fun Context.checkPermission(vararg permissions:String, function: () -> Unit){
    if(permissions.any { ContextCompat.checkSelfPermission(this , it) != PackageManager.PERMISSION_GRANTED }){
        function()
    }
}

fun Context.isGrantedPermission(vararg permissions: String , function: () -> Unit){
    if(permissions.all { ContextCompat.checkSelfPermission(this , it) == PackageManager.PERMISSION_GRANTED }){
        function()
    }
}


fun <T> lazyFast(operation: () -> T):Lazy<T> = lazy(LazyThreadSafetyMode.NONE){
    operation()
}

fun createUniqueFileName() :String{

    val sdf = SimpleDateFormat("/yyyy.MM.dd_HH:mm:ss z")

    return String.format("%s.%s", sdf.format(Date()), Random().nextInt(9))
}

fun ViewGroup.forEachChildren(function: (view : View) -> Unit) {
    for (i in 0 until childCount){
        function(getChildAt(i))
    }
}

