package com.bbva.hackaton.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.bbva.hackaton.R
import com.bbva.hackaton.adapter.PromocionAdapter


class HomeFragment : Fragment() {

    lateinit var viewHome : View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            viewHome = inflater!!.inflate(R.layout.fragment_home_bbva, container, false)
        return viewHome
    }

    override fun onPause() {
        super.onPause()
        (viewHome.parent as ViewGroup).removeView(viewHome)
    }

    var adapter:PromocionAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    /*    val listaPromocion = viewHome.findViewById(R.id.list_promocion) as ListView
        val promocion1 = Promocion("HORNERO", "75% de descuento en toda la carta")
        val promocion2 = Promocion("HORNERO", "75% de descuento en toda la carta")

        val promocion3 = Promocion("HORNERO", "75% de descuento en toda la carta")


        val list: ArrayList<Promocion> = ArrayList()
        list.add(promocion1)
        list.add(promocion2)

        list.add(promocion3)

        adapter = PromocionAdapter(context!!, list)
        listaPromocion.adapter = adapter*/
    }


}