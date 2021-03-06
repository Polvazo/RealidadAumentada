package com.bbva.hackaton.api

import com.corebuild.arlocation.demo.api.AuthAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {


    object ApiUtils {

        val BASE_URL = "https://infamous-backend.herokuapp.com/"

        val apiService: AuthAPI
            get() = RetrofitClient.getClient(BASE_URL)!!.create(AuthAPI::class.java)

    }

    object RetrofitClient {

        var retrofit: Retrofit? = null

        fun getClient(baseUrl: String): Retrofit? {
            if (retrofit == null) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build()

                retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit

        }
    }
}