package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.impl.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.data.impl.PlayListRepositoryImpl
import com.example.playlistmaker.data.impl.TrackAddToPlaylistRepositoryImpl
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.api.TrackAddToPlaylistInteractor
import com.example.playlistmaker.domain.impl.FavoriteTracksInteractor
import com.example.playlistmaker.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.domain.impl.TrackAddToPlaylistInteractorImpl
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.domain.repository.PlayListRepository
import com.example.playlistmaker.domain.repository.TrackAddToPlaylistRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { AppDatabase.getDatabase(get()) }
    single { get<AppDatabase>().trackDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().playlistTrackDao() }
    single { get<AppDatabase>().playlistTrackCrossRefDao() }

    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
    single<FavoriteTracksInteractor> { FavoriteTracksInteractor(get()) }
    single<PlayListRepository> { PlayListRepositoryImpl(get(), get(), get()) }
    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }

    single<TrackAddToPlaylistRepository> { TrackAddToPlaylistRepositoryImpl(get(), get()) }
    single<TrackAddToPlaylistInteractor> { TrackAddToPlaylistInteractorImpl(get()) }

}
