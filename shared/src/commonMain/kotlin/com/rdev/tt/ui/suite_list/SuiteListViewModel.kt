package com.rdev.tt.ui.suite_list

import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Suite
import com.rdev.tt.data.AppRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SuiteItem(
    val suite: Suite,
    val questionCount: Int,
    val learnedQuestionCount: Int
)

sealed interface SuiteListUiState {
    data object Loading : SuiteListUiState
    data class Error(val e: Throwable) : SuiteListUiState
    data class Content(
        val suites: List<SuiteItem>,
        val allQuestionCount: Int,
        val learnedQuestionCount: Int
    ) : SuiteListUiState
}

class SuiteListViewModel(
    private val appRepo: AppRepository
) : ViewModel() {
    private val _categoryFlow = MutableStateFlow<@Category String>(Category.BTT)

    private val _uiState = MutableStateFlow<SuiteListUiState>(SuiteListUiState.Loading)
    val uiState: StateFlow<SuiteListUiState> = _uiState

    init {
        viewModelScope.launch {
            _categoryFlow.collect {
                appRepo.getQuestionIdsByCategory(it)
                    .onSuccess { ids -> loadSuites(ids) }
                    .onFailure { e ->
                        _uiState.value = SuiteListUiState.Error(e)
                    }
            }
        }
    }

    private fun loadSuites(eligibleIds: List<Long>) {
        _uiState.value = SuiteListUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val learnedQuestionIds = appRepo.getLearnedQuestionIds(eligibleIds).getOrThrow()
                val result = appRepo.getSuitesWithQuestionCount(eligibleIds).getOrThrow()

                val items = result.filter { it.questionCount > 0L }.map {
                    val learnedCount =
                        appRepo.countLearnedQuestions(it.id, learnedQuestionIds).getOrThrow()
                            .toInt()
                    SuiteItem(
                        Suite(it.id, it.name),
                        questionCount = it.questionCount.toInt(),
                        learnedQuestionCount = learnedCount
                    )
                }

                _uiState.value = SuiteListUiState.Content(
                    suites = items,
                    allQuestionCount = items.sumOf { it.questionCount },
                    learnedQuestionCount = learnedQuestionIds.size,
                )
            }.onFailure { e ->
                _uiState.value = SuiteListUiState.Error(e)
            }
        }
    }
}