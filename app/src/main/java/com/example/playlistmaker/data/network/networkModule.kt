package com.example.playlistmaker.data.network

import com.example.playlistmaker.Constants.BASE_URL_ITUNES
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.api.ApiService
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.repository.TrackRepository
import com.google.gson.Gson
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL_ITUNES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>().create(ApiService::class.java) }

    factory<TrackRepository> { TrackRepositoryImpl(get()) }

    factory { SearchTracksInteractorImpl(get()) }
    single { Gson() }


}