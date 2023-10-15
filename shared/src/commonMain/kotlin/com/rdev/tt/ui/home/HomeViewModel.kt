package com.rdev.tt.ui.home

import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.core_model.Suite
import com.rdev.tt.data.AppRepository
import com.rdev.tt.data.mapper.toModel
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SuiteItem(
    val suite: Suite,
    val questionCount: Int,
    val learnedQuestionCount: Int
)

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Error(val e: Throwable) : HomeUiState
    data class Content(
        val suites: List<SuiteItem>,
        val allQuestionCount: Int,
        val learnedQuestionCount: Int,
        val visitedQuestionCount: Int
    ) : HomeUiState
}

class HomeViewModel(
    private val appRepo: AppRepository
) : ViewModel() {
    private val _categoryFlow = MutableStateFlow<@Category String>(Category.BTT)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            _categoryFlow.collect { loadSuites(it) }
        }
    }

    suspend fun getWronglyAnsweredQuestions(): List<Question> {
        return appRepo.getWronglyAnsweredQuestions().getOrElse { emptyList() }
    }

    private fun loadSuites(category: @Category String) {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val suites = doLoadSuites(category)
                val visitedCount = appRepo.countVisitedQuestions().getOrThrow().toInt()

                _uiState.value = HomeUiState.Content(
                    suites = suites,
                    allQuestionCount = suites.sumOf { it.questionCount },
                    learnedQuestionCount = suites.sumOf { it.learnedQuestionCount },
                    visitedQuestionCount = visitedCount
                )
            }.onFailure { e -> _uiState.value = HomeUiState.Error(e) }
        }
    }

    private suspend fun doLoadSuites(category: @Category String): List<SuiteItem> {
        val entities = appRepo.getAllSuites(category).getOrThrow()

        return entities.map { entity ->
            val questionIds = appRepo.getQuestionIdsFromSuite(entity.id).getOrThrow()
            val learnedQuestionIds = appRepo.getLearnedQuestionIds(questionIds).getOrThrow()

            SuiteItem(entity.toModel(), questionIds.size, learnedQuestionIds.size)
        }
    }
}