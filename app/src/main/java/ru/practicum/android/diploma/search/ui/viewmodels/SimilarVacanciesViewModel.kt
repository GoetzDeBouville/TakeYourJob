package ru.practicum.android.diploma.search.ui.viewmodels

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.data.dto.Resource
import ru.practicum.android.diploma.common.domain.models.NetworkErrors
import ru.practicum.android.diploma.filter.domain.FilterSettingsInteractor
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.models.VacanciesSearchResult
import ru.practicum.android.diploma.search.ui.viewmodels.states.ErrorsSearchScreenStates
import ru.practicum.android.diploma.search.ui.viewmodels.states.SearchScreenState
import ru.practicum.android.diploma.search.ui.viewmodels.states.SearchSettingsState
import javax.inject.Inject

@HiltViewModel
class SimilarVacanciesViewModel @Inject constructor(
    private val interactor: SearchInteractor,
    private val sharedPrefsInteractor: FilterSettingsInteractor
) : SearchViewModel(
    interactor,
    sharedPrefsInteractor
) {
    private var searchSettings: SearchSettingsState = SearchSettingsState()
    private var _state = MutableStateFlow<SearchScreenState>(SearchScreenState.Default())
    override val state: StateFlow<SearchScreenState>
        get() = _state

    var vacancyId: Int = 0

    fun getVacancyList(id: Int) {
        viewModelScope.launch {
            interactor.searchSimilarVacancies(id)
                .collect { result ->
                    provideResponse(result)
                }
        }
    }

    private fun provideResponse(result: Resource<VacanciesSearchResult>) {
        when (result) {
            is Resource.Success -> {
                if (result.data?.vacancies.isNullOrEmpty()) {
                    _state.value = SearchScreenState.Error(ErrorsSearchScreenStates.NOT_FOUND)
                } else if (result.data!!.vacanciesFound > 0) {
                    _state.value = SearchScreenState.Content(
                        result.data.totalPages,
                        result.data.currentPage,
                        result.data.vacancies.toList().size,
                        result.data.vacancies.toList()
                    )
                }
            }

            is Resource.Error -> {
                _state.value = SearchScreenState.Error(
                    when (result.error) {
                        NetworkErrors.NoInternet -> ErrorsSearchScreenStates.NO_INTERNET
                        else -> ErrorsSearchScreenStates.SERVER_ERROR
                    }
                )
            }
        }
    }
}
