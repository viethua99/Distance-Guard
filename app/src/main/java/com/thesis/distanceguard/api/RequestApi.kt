package com.thesis.distanceguard.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.TimeUnit

class RequestApi : Callback<World> {
    lateinit var listener: RequestListener
    val BASE_URL: String = "https://disease.sh"

    fun excuteRequestAPI(requestListener: RequestListener) {
        try {
            listener = requestListener

            val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            // prepare call in Retrofit
            val api: GetApi = retrofit.create(GetApi::class.java)
            val call: Call<World> = api.getCovidWorld()
            call.enqueue(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResponse(call: Call<World>, response: Response<World>) {
        response.body()?.let { listener.didGetDataResponse("" + response.code(), it) }
    }

    override fun onFailure(call: Call<World>, t: Throwable) {
        Timber.d("onFailure " + t.message)
    }

    // callback data onResponse /onFailure
    interface RequestListener {
        fun didGetDataResponse(message: String, result: World)
    }

    // Interface request GET API
    interface GetApi {
        @GET("/v3/covid-19/all")
        fun getCovidWorld(): Call<World>
    }
}
