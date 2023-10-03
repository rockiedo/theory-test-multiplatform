package com.rdev.tt.ui.test_suite

import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.data.AppRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface TestSuiteState {
    data object Loading : TestSuiteState
    data object Error : TestSuiteState
    data class Content(val questions: List<Question>) : TestSuiteState
}

class TestSuiteViewModel(
    private val appRepo: AppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<TestSuiteState>(TestSuiteState.Loading)
    val uiState: StateFlow<TestSuiteState> = _uiState

    fun loadTestSuite(suiteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val questions = appRepo.loadQuestionFromSuite(suiteId).getOrThrow()
                if (questions.isEmpty()) throw IllegalStateException("Empty question list")

                _uiState.value = TestSuiteState.Content(questions)
            }.onFailure {
                _uiState.value = TestSuiteState.Error
            }
        }
    }

    fun getImageFilePath(name: String?, category: @Category String): String? {
        if (name.isNullOrEmpty()) return null
        return "${appRepo.getImageDir(category)}/$name.png"
    }

    fun recordAnswer(questionId: Long, isCorrect: Boolean) {
        viewModelScope.launch {
            appRepo.recordAnswer(questionId, isCorrect)
        }
    }
}