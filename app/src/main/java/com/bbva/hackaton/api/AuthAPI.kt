package com.corebuild.arlocation.demo.api

import com.bumptech.glide.request.SingleRequest
import com.corebuild.arlocation.demo.model.Comercio
import com.corebuild.arlocation.demo.model.User
import com.corebuild.arlocation.demo.model.VenueWrapper
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface AuthAPI {

    @POST("login")
    fun auth (@Body request: User): Call<User>


    @GET("comercio/all/v1")
    fun getComercio ( ): Call<ArrayList<Comercio>>

    @GET("comercio/{comercio}")
    fun getComercioPorRuc (@Path("comercio") ruc : Int): Call<Comercio>

}