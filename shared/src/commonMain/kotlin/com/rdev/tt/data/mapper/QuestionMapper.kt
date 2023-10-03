package com.rdev.tt.data.mapper

import com.rdev.tt.core_model.Question
import com.rdev.tt.data.database.QuestionEntity

fun QuestionEntity.toModel() = Question(
    id = this.id,
    question = this.question,
    choices = this.choices,
    answerIdx = this.answerIdx.toInt(),
    explanation = this.explanation,
    image = this.image,
    categories = this.categories,
)