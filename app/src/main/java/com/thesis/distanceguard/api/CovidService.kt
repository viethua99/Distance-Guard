package com.thesis.distanceguard.api

import com.thesis.distanceguard.presentation.dashboard.DashboardFragment
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit

class CovidService {
    companion object {
        private const val BASE_URL: String = "https://disease.sh"
        private var api: CovidApi? = null

        fun getApi(): CovidApi = api ?: synchronized(this) {
            api ?: createInstance().also {
                api = it
            }
        }

        private fun createInstance(): CovidApi {
            val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
//                .baseUrl(DashboardFragment.BASE_URL)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(CovidApi::class.java)
        }
    }
}