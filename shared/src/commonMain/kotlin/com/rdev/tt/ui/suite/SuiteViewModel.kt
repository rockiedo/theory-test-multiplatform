package com.rdev.tt.ui.suite

import com.rdev.tt.core_model.Question
import com.rdev.tt.data.AppRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SuiteState {
    data object Loading : SuiteState
    data object Error : SuiteState
    data class Content(val questions: List<Question>) : SuiteState
}

class SuiteViewModel(
    private val appRepo: AppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SuiteState>(SuiteState.Loading)
    val uiState: StateFlow<SuiteState> = _uiState

    fun loadSuite(suiteId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                val questions = appRepo.loadQuestionFromSuite(suiteId).getOrThrow()
                if (questions.isEmpty()) throw IllegalStateException("Empty question list")

                _uiState.value = SuiteState.Content(questions)
            }.onFailure {
                _uiState.value = SuiteState.Error
            }
        }
    }

    fun recordAnswer(questionId: Long, isCorrect: Boolean) {
        viewModelScope.launch {
            appRepo.recordAnswer(questionId, isCorrect)
        }
    }
}