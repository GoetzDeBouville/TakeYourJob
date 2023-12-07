package ru.practicum.android.diploma.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.practicum.android.diploma.common.data.db.AppDatabase
import ru.practicum.android.diploma.search.data.network.HHSearchRepository
import ru.practicum.android.diploma.search.domain.api.FavoritesDBConverter
import ru.practicum.android.diploma.search.domain.api.FavoritesDBInteractor
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.api.SearchResultConverter
import ru.practicum.android.diploma.search.domain.api.SingleVacancyConverter
import ru.practicum.android.diploma.search.domain.api.SingleVacancyInteractor
import ru.practicum.android.diploma.search.domain.impl.FavoritesDBInteractorImpl
import ru.practicum.android.diploma.search.domain.impl.SearchInteractorImpl
import ru.practicum.android.diploma.search.domain.impl.SingleVacancyInteractorImpl

@Module
@InstallIn(ViewModelComponent::class)
class InteractorModule {
    @Provides
    fun providesSearchInteractor(
        repository: HHSearchRepository,
        converter: SearchResultConverter
    ): SearchInteractor = SearchInteractorImpl(repository, converter)

    @Provides
    fun providesFavoritesDbInteractor(
        database: AppDatabase,
        converter: FavoritesDBConverter
    ): FavoritesDBInteractor = FavoritesDBInteractorImpl(database, converter)

    @Provides
    fun providesSingleVacancyInteractor(
        repository: HHSearchRepository,
        converter: SingleVacancyConverter,
        database: AppDatabase
    ): SingleVacancyInteractor = SingleVacancyInteractorImpl(repository, converter, database)
}
