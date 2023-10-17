package com.rdev.tt.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.core_model.Suite
import com.rdev.tt.data.AppRepository
import com.rdev.tt.data.mapper.toModel
import com.rdev.tt.ui.BusEvent
import com.rdev.tt.ui.NavigationBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
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
        val categoryDisplay: String,
        val suites: List<SuiteItem>,
        val allQuestionCount: Int,
        val learnedQuestionCount: Int,
        val visitedQuestionCount: Int
    ) : HomeUiState
}

class HomeViewModel(
    private val appRepo: AppRepository,
    private val navigationBus: NavigationBus
) : ScreenModel {
    private val _categoryFlow = MutableStateFlow<@Category String>(Category.BTT)
    val categoryFlow: StateFlow<@Category String> = _categoryFlow

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        coroutineScope.launch {
            _categoryFlow.collect {
                _uiState.value = HomeUiState.Loading
                loadSuites(it)
            }
        }

        coroutineScope.launch {
            navigationBus.eventStream.collect { event ->
                if (event !is BusEvent.HistoryChanged) return@collect
                loadSuites(_categoryFlow.value)
            }
        }
    }

    fun setCategory(category: @Category String) {
        _categoryFlow.value = category
    }

    suspend fun getWronglyAnsweredQuestions(): List<Question> {
        return appRepo.getWronglyAnsweredQuestions().getOrElse { emptyList() }
    }

    private fun loadSuites(category: @Category String) {
        coroutineScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val suites = doLoadSuites(category)
                val visitedCount = appRepo.countVisitedQuestions().getOrThrow().toInt()

                val categoryDisplay = when (category) {
                    Category.BTT -> "Basic Theory Test (BTT)"
                    Category.FTT -> "Final Theory Test (FTT)"
                    else -> "Learning progress"
                }

                delay(200)

                _uiState.value = HomeUiState.Content(
                    categoryDisplay = categoryDisplay,
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