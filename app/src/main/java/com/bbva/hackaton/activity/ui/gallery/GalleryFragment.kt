package com.bbva.hackaton.activity.ui.gallery

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bbva.hackaton.R
import com.bbva.hackaton.activity.DetallePromocion
import com.corebuild.arlocation.demo.model.Comercio
import com.corebuild.arlocation.demo.model.Geolocation
import com.corebuild.arlocation.demo.utils.AugmentedRealityLocationUtils
import com.corebuild.arlocation.demo.utils.PermissionUtils
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import kotlinx.android.synthetic.main.fragment_ubicacion.*
import kotlinx.android.synthetic.main.location_layout_renderable.view.*
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import java.lang.ref.WeakReference
import java.util.concurrent.CompletableFuture

class GalleryFragment : Fragment() {

    private var arCoreInstallRequested = false

    // Our ARCore-Location scene
    private var locationScene: LocationScene? = null

    private var arHandler = Handler(Looper.getMainLooper())

    lateinit var loadingDialog: AlertDialog

    private val resumeArElementsTask = Runnable {
        locationScene?.resume()
        arSceneView.resume()
    }

    private var userGeolocation = Geolocation.EMPTY_GEOLOCATION

    private var promocionesSet: MutableSet<Comercio> = mutableSetOf()
    private var areAllMarkersLoaded = false
    lateinit var viewUbicacion: View;

    private var markerRenderable: ModelRenderable? = null

    private var rotationAnimation: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewUbicacion = inflater!!.inflate(R.layout.fragment_ubicacion, container, false)

        setupLoadingDialog()

        return viewUbicacion
    }

    override fun onResume() {
        super.onResume()
        checkAndRequestPermissions()
    }

    override fun onPause() {
        super.onPause()
        arSceneView.session?.let {
            locationScene?.pause()
            arSceneView?.pause()
        }
    }

    private fun setupLoadingDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val dialogHintMainView =
            LayoutInflater.from(activity).inflate(R.layout.loading_dialog, null) as LinearLayout
        alertDialogBuilder.setView(dialogHintMainView)
        loadingDialog = alertDialogBuilder.create()
        loadingDialog.setCanceledOnTouchOutside(false)
    }

    private fun setupSession() {
        if (arSceneView == null) {
            return
        }

        if (arSceneView.session == null) {
            try {
                val session = AugmentedRealityLocationUtils.setupSession(
                    requireActivity(),
                    arCoreInstallRequested
                )
                if (session == null) {
                    arCoreInstallRequested = true
                    return
                } else {
                    arSceneView.setupSession(session)
                }
            } catch (e: UnavailableException) {
                AugmentedRealityLocationUtils.handleSessionException(requireActivity(), e)
            }
        }

        if (locationScene == null) {
            locationScene = LocationScene(requireActivity(), arSceneView)
            locationScene!!.setMinimalRefreshing(true)
            locationScene!!.setOffsetOverlapping(true)
//            locationScene!!.setRemoveOverlapping(true)
            locationScene!!.anchorRefreshInterval = 2000
        }

        try {
            resumeArElementsTask.run()
        } catch (e: CameraNotAvailableException) {
            Toast.makeText(requireActivity(), "Unable to get camera", Toast.LENGTH_LONG).show()
            activity?.finish()
            return
        }

        if (userGeolocation == Geolocation.EMPTY_GEOLOCATION) {
            LocationAsyncTask(WeakReference(this)).execute(locationScene!!)
        }

        ModelRenderable.builder()
            .setSource(requireActivity(), Uri.parse("model.sfb"))
            .build()
            .thenAccept { renderable -> markerRenderable = renderable }
            .exceptionally { throwable ->
                val toast =
                    Toast.makeText(
                        requireActivity(),
                        "Unable to load andy renderable",
                        Toast.LENGTH_LONG
                    )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                null
            }

        var mListComercio =
            activity!!.intent.getSerializableExtra("mListaComercio") as ArrayList<Comercio>
        promocionesSet.clear()
        promocionesSet.addAll(mListComercio)
        areAllMarkersLoaded = false
        locationScene!!.clearMarkers()
        renderOfertas()


    }

    private fun fetchOfertas(deviceLatitude: Double, deviceLongitude: Double) {
        loadingDialog.dismiss()
        userGeolocation = Geolocation(deviceLatitude.toString(), deviceLongitude.toString())
    }

    private fun renderOfertas() {
        setupAndRenderOfertasMarkers()
        updateOfertasMarkers()
    }

    private fun setupAndRenderOfertasMarkers() {
        promocionesSet.forEach { oferta ->
            val completableFutureViewRenderable = ViewRenderable.builder()
                .setView(requireActivity(), R.layout.location_layout_renderable)
                .build()
            CompletableFuture.anyOf(completableFutureViewRenderable)
                .handle<Any> { _, throwable ->
                    //here we know the renderable was built or not
                    if (throwable != null) {
                        // handle renderable load fail
                        return@handle null
                    }
                    try {
                        val ofertaMarker = LocationMarker(
                            oferta.longitud!!.toDouble(),
                            oferta.latitud!!.toDouble(),
                            setOfertaNode(oferta, completableFutureViewRenderable)
                        )
                        arHandler.postDelayed({
                            attachMarkerToScene(
                                ofertaMarker,
                                completableFutureViewRenderable.get().view
                            )
                            if (promocionesSet.indexOf(oferta) == promocionesSet.size - 1) {
                                areAllMarkersLoaded = true
                            }
                        }, 200)

                    } catch (ex: Exception) {
                        //                        showToast(getString(R.string.generic_error_msg))
                    }
                    null
                }
        }
    }

    private fun updateOfertasMarkers() {
        arSceneView.scene.addOnUpdateListener()
        {
            if (!areAllMarkersLoaded) {
                return@addOnUpdateListener
            }

            locationScene?.mLocationMarkers?.forEach { locationMarker ->
                locationMarker.height =
                    AugmentedRealityLocationUtils.generateRandomHeightBasedOnDistance(
                        locationMarker?.anchorNode?.distance ?: 0
                    )
            }


            val frame = arSceneView!!.arFrame ?: return@addOnUpdateListener
            if (frame.camera.trackingState != TrackingState.TRACKING) {
                return@addOnUpdateListener
            }
            locationScene!!.processFrame(frame)
        }
    }


    private fun attachMarkerToScene(
        locationMarker: LocationMarker,
        layoutRendarable: View
    ) {
        resumeArElementsTask.run {
            locationMarker.scalingMode = LocationMarker.ScalingMode.FIXED_SIZE_ON_SCREEN
            locationMarker.scaleModifier =
                AugmentedRealityLocationUtils.INITIAL_MARKER_SCALE_MODIFIER

            locationScene?.mLocationMarkers?.add(locationMarker)
            locationMarker.anchorNode?.isEnabled = true

            arHandler.post {
                locationScene?.refreshAnchors()
                layoutRendarable.pinContainer.visibility = View.VISIBLE
            }
        }
        locationMarker.setRenderEvent { locationNode ->
            layoutRendarable.distance.text =
                AugmentedRealityLocationUtils.showDistance(locationNode.distance)
            resumeArElementsTask.run {
                computeNewScaleModifierBasedOnDistance(locationMarker, locationNode.distance)
            }
        }
    }

    private fun computeNewScaleModifierBasedOnDistance(
        locationMarker: LocationMarker,
        distance: Int
    ) {
        val scaleModifier =
            AugmentedRealityLocationUtils.getScaleModifierBasedOnRealDistance(distance)
        return if (scaleModifier == AugmentedRealityLocationUtils.INVALID_MARKER_SCALE_MODIFIER) {
            detachMarker(locationMarker)
        } else {
            locationMarker.scaleModifier = scaleModifier
        }
    }

    private fun detachMarker(locationMarker: LocationMarker) {
        locationMarker.anchorNode?.anchor?.detach()
        locationMarker.anchorNode?.isEnabled = false
        locationMarker.anchorNode = null
    }


    private fun setOfertaNode(
        oferta: Comercio,
        completableFuture: CompletableFuture<ViewRenderable>
    ): Node {
        val node = Node()
        node.renderable = completableFuture.get()

        val nodeLayout = completableFuture.get().view
        val comercioName = nodeLayout.name
        val markerLayoutContainer = nodeLayout.pinContainer
        comercioName.text = oferta.nombreComercialMarca
        markerLayoutContainer.visibility = View.GONE
        nodeLayout.setOnTouchListener { _, _ ->

            val intent = Intent(context, DetallePromocion::class.java)
            intent.putExtra("mListaComercio", oferta)
            startActivity(intent)

            false
        }


        val marker = Node()

        marker.setLocalScale(Vector3(0.06f, 0.06f, 0.06f))
        marker.setLocalPosition(Vector3(0.22f, 0.05f, -0.03f))
        marker.setParent(node)
        marker.renderable = markerRenderable


        rotationAnimation = createAnimator()
        rotationAnimation!!.setTarget(marker)
        rotationAnimation!!.setDuration(4000) //(1000 * 360 / 90.0f * 1.0f) as Long
        rotationAnimation!!.start()


        return node
    }

    private fun createAnimator(): ObjectAnimator {

        val orientation1 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 0f)
        val orientation2 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 120f)
        val orientation3 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 240f)
        val orientation4 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 360f)

        val orbitAnimation = ObjectAnimator()
        orbitAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4)

        // Next, give it the localRotation property.
        orbitAnimation.propertyName = "localRotation"

        // Use Sceneform's QuaternionEvaluator.
        orbitAnimation.setEvaluator(QuaternionEvaluator())

        //  Allow orbitAnimation to repeat forever
        orbitAnimation.repeatCount = ObjectAnimator.INFINITE
        orbitAnimation.repeatMode = ObjectAnimator.RESTART
        orbitAnimation.interpolator = LinearInterpolator()
        orbitAnimation.setAutoCancel(true)

        return orbitAnimation
    }


    private fun checkAndRequestPermissions() {
        if (!PermissionUtils.hasLocationAndCameraPermissions(requireActivity())) {
            PermissionUtils.requestCameraAndLocationPermissions(requireActivity())
        } else {
            setupSession()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        if (!PermissionUtils.hasLocationAndCameraPermissions(requireActivity())) {
            Toast.makeText(
                activity, R.string.camera_and_location_permission_request, Toast.LENGTH_LONG
            )
                .show()
            if (!PermissionUtils.shouldShowRequestPermissionRationale(requireActivity())) {
                // Permission denied with checking "Do not ask again".
                PermissionUtils.launchPermissionSettings(requireActivity())
            }
            activity?.finish()
        }
    }

    class LocationAsyncTask(private val activityWeakReference: WeakReference<GalleryFragment>) :
        AsyncTask<LocationScene, Void, List<Double>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            activityWeakReference.get()!!.loadingDialog.show()
        }

        override fun doInBackground(vararg p0: LocationScene): List<Double> {
            var deviceLatitude: Double?
            var deviceLongitude: Double?
            do {
                //deviceLatitude = p0[0].deviceLocation?.currentBestLocation?.latitude
                // deviceLongitude = p0[0].deviceLocation?.currentBestLocation?.longitude
                deviceLatitude = -12.09424
                deviceLongitude = -12.09424
            } while (deviceLatitude == null || deviceLongitude == null)
            return listOf(deviceLatitude, deviceLongitude)
        }

        override fun onPostExecute(geolocation: List<Double>) {
            activityWeakReference.get()!!.fetchOfertas(
                //deviceLatitude = geolocation[0],
                //deviceLongitude = geolocation[1]
                deviceLatitude = -12.09424,
                deviceLongitude = -12.09424
            )
            super.onPostExecute(geolocation)
        }
    }
}