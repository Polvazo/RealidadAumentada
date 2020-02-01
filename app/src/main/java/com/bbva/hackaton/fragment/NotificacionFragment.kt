package com.bbva.hackaton.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bbva.hackaton.R
import com.corebuild.arlocation.demo.model.Comercio
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.bbva.hackaton.activity.DetallePromocion
import com.bbva.hackaton.adapter.AdapterPromocion
import com.google.android.gms.maps.model.Marker
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.*





class NotificacionFragment : Fragment(), OnMapReadyCallback, AdapterPromocion.OnNoteListener {


    private var googleMap: GoogleMap? = null

    private var selectedMarker: Marker? = null
    val hashMap: HashMap<String, String?> = HashMap<String, String?>()

    val itemTouchHelperCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }
        }





    var mListComercioFinal: ArrayList<Comercio>? = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_notificaciones, container, false)

        val listaPromocion = view.findViewById(R.id.lista_comercio) as RecyclerView
        val btnQr = view.findViewById(R.id.btn_changed_view) as ImageView

        btnQr.setOnClickListener(View.OnClickListener {

            Log.d("Pasop or aca ", "Este es el final")
            val fm = fragmentManager as FragmentManager?
            val ft = fm!!.beginTransaction()
            ft.replace(R.id.nav_host_fragment, UbicacionFragment())
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ft.commit()
        })

        var mListComercio = activity!!.intent.getSerializableExtra("mListaComercio") as ArrayList<Comercio>
        //listaPromocion.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val manager = childFragmentManager



        listaPromocion.adapter = AdapterPromocion(requireContext(), mListComercio, this,manager)



        mListComercioFinal = mListComercio






        return view
    }


    override fun onMapReady(p0: GoogleMap?) {


        googleMap = p0

        for (comercio in mListComercioFinal as ArrayList<Comercio>) {
            val latLng2 = LatLng(comercio.latitud!!.toString().toDouble(), comercio.longitud!!.toString().toDouble())
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng2)
            val marker: Marker
            marker = googleMap!!.addMarker(
                markerOptions
            )
            hashMap.put(marker.id, comercio.convenio)

        }
        val latLng = LatLng(-12.09424, -77.020481)
        val zoomLevel = 16.0f //This goes up to 21
        googleMap.let {
            it!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        }
        googleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                val convenioCode = hashMap.get(marker.id)
                var itemView : Comercio = Comercio()
                val filtered = mListComercioFinal!!.filter { it.convenio!!.toInt()==convenioCode!!.toInt() }
                filtered.forEach {
                    itemView  = it
                }
                Log.d("El convenio es ", "" + itemView.posicionView)

                return false
            }
        })

    }



    override fun onNoteClick(position: Int) {

        val intent = Intent(context, DetallePromocion::class.java)
        for (item in mListComercioFinal as ArrayList<Comercio>) {
            if (item.convenio!!.toInt() == position) {
                intent.putExtra("mListaComercio", item)
                startActivity(intent)
            }
        }

    }



}