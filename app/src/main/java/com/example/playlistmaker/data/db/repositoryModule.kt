package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.impl.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.data.impl.PlayListRepositoryImpl
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.impl.FavoriteTracksInteractor
import com.example.playlistmaker.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.domain.repository.PlayListRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { AppDatabase.getDatabase(get()) }
    single { get<AppDatabase>().trackDao() }
    single { get<AppDatabase>().playlistDao() }

    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
    single<FavoriteTracksInteractor> { FavoriteTracksInteractor(get()) }

    single<PlayListRepository> { PlayListRepositoryImpl(get(), get()) }
    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }
}
