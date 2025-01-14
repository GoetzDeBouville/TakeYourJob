package ru.practicum.android.diploma.filter.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.filter.data.dto.FilterSettingsDto
import ru.practicum.android.diploma.filter.domain.FilterSettingsRepository

class FilterSettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
) : FilterSettingsRepository, CoroutineScope by CoroutineScope(
    Dispatchers.IO +
        SupervisorJob()
) {

    private val _settingsUpdateFlow = MutableSharedFlow<FilterSettingsDto>(replay = 1)
    val settingsUpdateFlow: Flow<FilterSettingsDto> = _settingsUpdateFlow.asSharedFlow()

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override suspend fun getFilterSettings(): FilterSettingsDto {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(key, null)
                ?.let { text ->
                    Gson().fromJson(text, FilterSettingsDto::class.java)
                }
                ?: FilterSettingsDto()
        }
    }

    override suspend fun saveFilterSettings(settings: FilterSettingsDto) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putString(key, Gson().toJson(settings)) }
        }
        _settingsUpdateFlow.emit(settings)
    }

    override fun settingsFlow(): Flow<FilterSettingsDto> = settingsUpdateFlow

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String?
    ) {
        if (this.key == key) {
            launch {
                // Вызываем suspend функцию getFilterSettings внутри корутины
                val settings = getFilterSettings()
                _settingsUpdateFlow.emit(settings)
            }
        }
    }
}
