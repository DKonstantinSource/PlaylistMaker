package com.example.playlistmaker.data.settings.sharing.imp

import com.example.playlistmaker.domain.settings.sharing.api.ExternalNavigatorInteractor
import com.example.playlistmaker.domain.settings.sharing.impl.ExternalNavigatorInteractorImpl
import com.example.playlistmaker.domain.settings.sharing.repository.ExternalNavigator
import org.koin.dsl.module

val externalNavigationModule = module {
    single<ExternalNavigatorInteractor> { ExternalNavigatorInteractorImpl(get()) }
    single<ExternalNavigator> { ExternalNavigatorImpl(get()) }
}