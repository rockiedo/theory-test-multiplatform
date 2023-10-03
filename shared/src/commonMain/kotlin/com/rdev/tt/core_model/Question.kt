package com.rdev.tt.core_model

import androidx.compose.runtime.Stable

@Stable
data class Question(
    val id: Long,
    val question: String,
    val choices: List<String>,
    val answerIdx: Int,
    val explanation: String?,
    val image: String? = null,
    val categories: List<@Category String> = emptyList()
)
