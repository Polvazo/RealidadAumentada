package com.bbva.hackaton.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bbva.hackaton.R
import com.bbva.hackaton.model.Promocion
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.corebuild.arlocation.demo.model.Comercio
import kotlinx.android.synthetic.main.activity_splash_screen.view.*
import kotlinx.android.synthetic.main.fragment_marker.view.*
import kotlinx.android.synthetic.main.fragment_marker.view.txt_nombre_lugar
import kotlinx.android.synthetic.main.item_list_promociones.view.*
import kotlinx.android.synthetic.main.location_layout_renderable.view.*
import java.io.InputStream



class PromocionAdapter (var context: Context, val items: ArrayList<Comercio>, val listener: AdapterView.OnItemClickListener) : RecyclerView.Adapter<ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_promociones, parent, false))
    }

    override fun getItemId(position: Int): Long {
        return items.get(position).idComercio!!.toLong()
    }

    override fun getItemCount(): Int {
        return items.size }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.nombreComercio?.text = items.get(position).nombreComercialMarca
        holder.promocion?.text = items.get(position).promocion

        Glide.with(context)
                .load(items.get(position).urlImage)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.imgPromocion)

    }
}
class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val nombreComercio = view.txt_nombre_lugar
    val promocion = view.txt_promocion
    val imgPromocion = view.img_promocion
}