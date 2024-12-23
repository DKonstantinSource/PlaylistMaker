package com.example.playlistmaker.data

import android.content.Context
import com.example.playlistmaker.Constants.SEARCH_HISTORY
import com.example.playlistmaker.data.impl.SearchHistoryImpl
import com.example.playlistmaker.domain.api.ManageSearchHistory
import com.example.playlistmaker.domain.impl.ManageSearchHistoryInteractorImpl
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val historyListModule = module {
    single { Gson() }
    single { androidContext().getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE) }
    single<SearchHistoryRepository> { SearchHistoryImpl(get(), get()) }
    factory<ManageSearchHistory> { ManageSearchHistoryInteractorImpl(get()) }

}