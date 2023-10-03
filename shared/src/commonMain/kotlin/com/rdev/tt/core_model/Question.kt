package com.rdev.tt.core_model

import androidx.compose.runtime.Stable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class Question(
    val id: Long,
    @SerialName("question")
    val question: String,
    @SerialName("choices")
    val choices: List<String>,
    @SerialName("answerIdx")
    val answerIdx: Int,
    @SerialName("explanation")
    val explanation: String?,
    @SerialName("image")
    val image: String? = null,
    @SerialName("categories")
    val categories: List<@Category String> = emptyList()
)
