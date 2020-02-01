package com.bbva.hackaton.activity.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bbva.hackaton.R
import com.corebuild.arlocation.demo.model.Comercio
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bbva.hackaton.activity.DetallePromocion
import com.bbva.hackaton.adapter.AdapterPromocion
import com.google.android.gms.maps.model.Marker
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bbva.hackaton.fragment.UbicacionFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory


class HomeFragment : Fragment(), OnMapReadyCallback, AdapterPromocion.OnNoteListener {


    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Represents a geographical location.
     */
    protected var mLastLocation: Location? = null


    var mapFragment : SupportMapFragment? = SupportMapFragment()
    private var googleMap: GoogleMap? = null

    private var selectedMarker: Marker? = null
    val hashMap: HashMap<String, String?> = HashMap<String, String?>()


    var mLatitud : Double = -77.020481
    var mLongitud : Double =  -12.09424

    var mListComercioFinal: ArrayList<Comercio>? = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_notificaciones, container, false)

        val listaPromocion = view.findViewById(R.id.lista_comercio) as RecyclerView
        var mListComercio =
            activity!!.intent.getSerializableExtra("mListaComercio") as ArrayList<Comercio>
        listaPromocion.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        val manager = childFragmentManager

        listaPromocion.adapter = AdapterPromocion(requireContext(), mListComercio, this, manager)


        mListComercioFinal = mListComercio



        listaPromocion.itemAnimator = DefaultItemAnimator()
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(listaPromocion)






        return view
    }

    override fun onMapReady(p0: GoogleMap?) {


        googleMap = p0

        val latLng = LatLng(mLatitud, mLongitud)
        Log.d("Longitud2 " , " " +  mLongitud)
        Log.d("mLatitud2 " , " " +  mLatitud)

        val markerOptions2 = MarkerOptions()
        markerOptions2.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

        val marker1: Marker
        marker1 = googleMap!!.addMarker(
            markerOptions2
        )


        val zoomLevel = 16.0f //This goes up to 21
        googleMap.let {
            it!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        }

        for (comercio in mListComercioFinal as ArrayList<Comercio>) {
            val latLng2 = LatLng(
                comercio.latitud!!.toString().toDouble(),
                comercio.longitud!!.toString().toDouble()
            )
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng2)
            val marker: Marker
            marker = googleMap!!.addMarker(
                markerOptions
            )
            hashMap.put(marker.id, comercio.convenio)

        }

        googleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                val convenioCode = hashMap.get(marker.id)
                var itemView: Comercio = Comercio()
                val filtered =
                    mListComercioFinal!!.filter { it.convenio!!.toInt() == convenioCode!!.toInt() }
                filtered.forEach {
                    itemView = it
                }
                Log.d("El convenio es ", "" + itemView.posicionView)

                return false
            }
        })

    }

    override fun onNoteClick(position: Int) {

        val intent = Intent(context, DetallePromocion::class.java)
        val comercio: Comercio = Comercio()
        for (item in mListComercioFinal as ArrayList<Comercio>) {
            if (item.convenio!!.toInt() == position) {
                intent.putExtra("mListaComercio", item)
                startActivity(intent)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()


        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                mLastLocation = task.result

                mLastLocation = task.result
                mLongitud =  (mLastLocation)!!.longitude
                mLatitud = (mLastLocation)!!.latitude

                Log.d("Longitud " , " " +  mLongitud)
                Log.d("mLatitud " , " " +  mLatitud)
                mapFragment?.getMapAsync(this)

            } else {
                Log.w(TAG, "getLastLocation:exception", task.exception)
            }
        }
    }

    /**
     * Shows a [] using `text`.
     * @param text The Snackbar text.
     */
    private fun showMessage(text: String) {
        val container = view!!.findViewById<View>(R.id.container_home)
        if (container != null) {
            Toast.makeText(requireActivity(), text, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Shows a [].
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * *
     * @param actionStringId   The text of the action item.
     * *
     * @param listener         The listener associated with the Snackbar action.
     */
    private fun showSnackbar(
        mainTextStringId: Int, actionStringId: Int,
        listener: View.OnClickListener
    ) {

    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")


        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest()
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation()
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.

            }
        }
    }

    companion object {

        private val TAG = "LocationProvider"

        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }
}