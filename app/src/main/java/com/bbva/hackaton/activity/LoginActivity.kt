package com.bbva.hackaton.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.bbva.hackaton.R
import com.bbva.hackaton.adapter.TipoDocumentoAdapter
import com.bbva.hackaton.api.RetrofitClient
import com.corebuild.arlocation.demo.api.AuthAPI
import com.corebuild.arlocation.demo.model.Comercio
import com.corebuild.arlocation.demo.model.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_documentos_identidad.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




class LoginActivity : AppCompatActivity() {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null

    private lateinit var tipo_documento : EditText
    private lateinit var btnLogin : Button
    private lateinit var etUsername : EditText
    private lateinit var etPassword : EditText
    private val listComercio: ArrayList<Comercio> = ArrayList()

    var mListComercio: ArrayList<Comercio>? = ArrayList()


    var mLatitud : Double = -12.09424
    var mLongitud : Double =  -77.020481


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        tipo_documento = findViewById(R.id.et_documento)
        btnLogin = findViewById(R.id.btn_login)
        etUsername = findViewById(R.id.et_num_documento)
        etPassword = findViewById(R.id.et_password)

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_documentos_identidad, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val opcionesDocumentos = mBuilder.create()
       // opcionesDocumentos.setCancelable(false)




        tipo_documento.setOnClickListener {

            opcionDocumento()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)




    }


     private fun  isLogin ()  {

         loading_login.visibility = View.VISIBLE
         val username = etUsername.text.toString()
         val password = etPassword.text.toString()
         var mAPIService: AuthAPI? = null

         mAPIService = RetrofitClient.ApiUtils.apiService

         val user2 =  User(username,password)


         mAPIService!!.auth(user2).enqueue(object : Callback<User> {

             override fun onResponse(call: Call<User>, response: Response<User>) {

                 if (response.isSuccessful()) {
                     cargarLugares()
                 } else {
                     loading_login.visibility = View.GONE
                     Toast.makeText(this@LoginActivity, "Usuario incorrecto", Toast.LENGTH_SHORT).show()

                 }
             }

             override fun onFailure(call: Call<User>, t: Throwable) {

             }
         })
    }

    private fun cargarLugares (){

        var mAPIService: AuthAPI? = null


        mAPIService = RetrofitClient.ApiUtils.apiService
        mAPIService.getComercio().enqueue(object : Callback<ArrayList<Comercio>> {

            override fun onResponse(call: Call<ArrayList<Comercio>>, response: Response<ArrayList<Comercio>>) {
                if (response.isSuccessful()) {

                    mListComercio = response.body()
                    val intent = Intent(this@LoginActivity, MenuActivity::class.java)
                    intent.putExtra("mListaComercio", mListComercio)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Falló carga de comercios.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<Comercio>>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error de servidor", Toast.LENGTH_SHORT).show()

            }
        })
    }

    fun opcionDocumento() {

        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val view = inflater.inflate(R.layout.dialog_documentos_identidad, null)
        val tipoDcumentos = view.findViewById<ListView>(R.id.id_documentos)

        builder.setView(view)
            .setCancelable(false)
        val alert11 = builder.create()
        val documentos : ArrayList<String> = ArrayList();
        documentos.add("DNI")
        documentos.add("RUC")
        documentos.add("CARNE DE EXTRANJERIA")
        documentos.add("OTROS")


        val myListAdapter = TipoDocumentoAdapter(this,documentos)
        tipoDcumentos.adapter = myListAdapter


        tipoDcumentos.setOnItemClickListener(){adapterView, view, position, id ->
            tipo_documento.setText(tipoDcumentos.getItemAtPosition(position).toString())
            alert11.dismiss()

        }

        alert11.show()
    }

    protected fun hideKeyboard() {
        val view = this.currentFocus
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            val imm: InputMethodManager = applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            view?.post({
                imm.hideSoftInputFromWindow(this.currentFocus.windowToken, 0)
                imm.hideSoftInputFromInputMethod(this.currentFocus.windowToken, 0)
            })
        } else {
            if (view != null) {
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                imm.hideSoftInputFromInputMethod(view.windowToken, 0)
            }
        }
    }




    override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
            btnLogin.setOnClickListener {
                hideKeyboard()
                isLogin()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                mLastLocation = task.result

                mLastLocation = task.result
                //mLongitud =  (mLastLocation)!!.longitude
               //mLatitud = (mLastLocation)!!.latitude

                Log.d("Longitud " , " " +  mLongitud)
                Log.d("mLatitud " , " " +  mLatitud)


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
        val container = findViewById<View>(R.id.container_home)
        if (container != null) {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
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
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
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
