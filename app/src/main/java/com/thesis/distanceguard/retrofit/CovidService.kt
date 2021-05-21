package com.thesis.distanceguard.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(CovidApi::class.java)
        }
    }
}