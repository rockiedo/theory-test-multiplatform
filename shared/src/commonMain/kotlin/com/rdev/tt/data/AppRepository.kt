package com.rdev.tt.data

import com.rdev.tt._utils.DateTimeUtils
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.data.database.AppDb
import com.rdev.tt.data.database.SuiteEntity
import com.rdev.tt.data.mapper.toModel

@Suppress("RedundantSuspendModifier")
class AppRepository(
    private val db: AppDb,
    private val dateTimeUtils: DateTimeUtils
) {
    suspend fun getAllSuites(category: @Category String): Result<List<SuiteEntity>> {
        return kotlin.runCatching {
            db.suiteEntityQueries
                .getAllSuites(category)
                .executeAsList()
        }
    }

    suspend fun getQuestionIdsFromSuite(suiteId: Long): Result<List<Long>> {
        return kotlin.runCatching {
            db.questionSuiteEntityQueries
                .getQuestionIdsFromSuite(suiteId)
                .executeAsList()
        }
    }

    suspend fun loadQuestionFromSuite(suiteId: Long): Result<List<Question>> {
        return kotlin.runCatching {
            val result = mutableListOf<Question>()

            db.transaction {
                val questionIds = db.questionSuiteEntityQueries
                    .getQuestionIdsFromSuite(suiteId)
                    .executeAsList()

                result.addAll(
                    db.questionEntityQueries
                        .getQuestionsByIds(questionIds)
                        .executeAsList()
                        .map { it.toModel() }
                )
            }

            result
        }
    }

    suspend fun recordAnswer(questionId: Long, isCorrect: Boolean): Result<Unit> {
        return kotlin.runCatching {
            db.historyEntityQueries
                .insertHistory(questionId, isCorrect, dateTimeUtils.getCurrentTimestamp())
        }
    }

    suspend fun getLearnedQuestionIds(
        eligibleQuestionIds: List<Long>
    ): Result<List<Long>> {
        return kotlin.runCatching {
            db.historyEntityQueries
                .getLearnedQuestionIdsFilteredByIds(eligibleQuestionIds)
                .executeAsList()
        }
    }

    suspend fun countVisitedQuestions(): Result<Long> {
        return kotlin.runCatching {
            db.historyEntityQueries.countVisitedQuestions().executeAsOne()
        }
    }

    suspend fun getWronglyAnsweredQuestions(): Result<List<Question>> {
        return kotlin.runCatching {
            db.questionEntityQueries
                .getWronglyAnsweredQuestions()
                .executeAsList()
                .map {
                    Question(
                        id = it.id,
                        question = it.question,
                        choices = it.choices,
                        answerIdx = it.answerIdx.toInt(),
                        explanation = it.explanation,
                        image = it.image,
                        categories = it.categories,
                    )
                }
        }
    }
    
    suspend fun filterWronglyAnsweredQuestions(ids: List<Long>): Result<List<Long>> {
        return kotlin.runCatching {
            db.questionEntityQueries
                .filterWronglyAnsweredQuestions(ids)
                .executeAsList()
        }
    }
}