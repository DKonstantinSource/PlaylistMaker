package com.example.playlistmaker.data.network.API


import com.example.playlistmaker.data.network.response.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<SearchResponse>
}