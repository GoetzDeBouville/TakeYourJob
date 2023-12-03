package ru.practicum.android.diploma.common.data.dto

data class VacanciesSearchRequest(
    val text: String = "",
    val page: Int = 0,
    val perPage: Int = 20,
    val area: Int? = null,
    val industry: Int? = null,
    val salary: Int? = null,
    val onlyWithSalary: Boolean = false
)
