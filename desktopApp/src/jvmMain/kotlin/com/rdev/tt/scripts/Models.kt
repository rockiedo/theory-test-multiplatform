package com.rdev.tt.scripts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionJson(
    @SerialName("question")
    val question: String,
    @SerialName("answer")
    val answer: Long,
    @SerialName("choice0")
    val firstChoice: String,
    @SerialName("choice1")
    val secondChoice: String,
    @SerialName("choice2")
    val thirdChoice: String,
    @SerialName("explaination")
    val explanation: String,
    @SerialName("imageName")
    val image: String
)

@Serializable
data class TestSuiteJson(
    @SerialName("type")
    val name: String,
    @SerialName("questions")
    val questions: List<QuestionJson>
)

@Serializable
data class Image(
    @SerialName("image")
    val name: String
)

@Serializable
data class ImageUsage(
    @SerialName("unusedImages")
    val unusedImages: List<String>,
    @SerialName("notFoundImages")
    val notFoundImages: List<String>,
    @SerialName("incorrectFormatImages")
    val incorrectFormatImages: List<String>
)