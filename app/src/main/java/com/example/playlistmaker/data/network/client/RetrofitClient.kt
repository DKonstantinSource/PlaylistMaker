package com.example.playlistmaker.data.network.client

import com.example.playlistmaker.Constants.BASE_URL_ITUNES
import com.example.playlistmaker.data.network.API.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_ITUNES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun createApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}