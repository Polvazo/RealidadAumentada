package com.bbva.hackaton.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import com.bbva.hackaton.R
import com.bbva.hackaton.model.MarkerData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.fragment_marker.view.*


class MarkerAdapter(val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker?): View {

        var mInfoView = (context as Activity).layoutInflater.inflate(R.layout.fragment_marker, null)
        var mInfoWindow: MarkerData? = p0?.tag as MarkerData?

        mInfoView.txt_nombre_lugar.text = mInfoWindow?.mLocatioName
        mInfoView.txt_direccion.text = mInfoWindow?.mLocationAddres


        return mInfoView
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
}