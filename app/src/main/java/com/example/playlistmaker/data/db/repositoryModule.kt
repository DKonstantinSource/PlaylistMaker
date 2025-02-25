package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.impl.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.domain.impl.FavoriteTracksInteractor
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { TrackDatabase.create(get()) }
    single { get<TrackDatabase>().trackDao() }
    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
    single { FavoriteTracksInteractor(get()) }
}
