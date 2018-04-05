package com.vutka.vision.emoji.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vutka.vision.emoji.R
import kotlinx.android.synthetic.main.missing_permission_layout.*

/**
 * Created by abidhasanshaon on 7/3/18.
 */
class MissingPermissionFragment : Fragment() {

    var openSettings : () -> Unit = {}

    private val TAG = MissingPermissionFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.missing_permission_layout, container , false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = super.onViewCreated(view , savedInstanceState).run {
        settings_btn.setOnClickListener {
            openSettings()
        }
    }

}