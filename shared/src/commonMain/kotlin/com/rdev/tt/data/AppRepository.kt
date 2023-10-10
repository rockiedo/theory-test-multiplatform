package com.rdev.tt.data

import com.rdev.tt._utils.DateTimeUtils
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.data.database.AppDb
import com.rdev.tt.data.database.GetSuitesWithQuestionCountByQuestionIds
import com.rdev.tt.data.mapper.toModel

@Suppress("RedundantSuspendModifier")
class AppRepository(
    private val db: AppDb,
    private val dateTimeUtils: DateTimeUtils
) {
    suspend fun getSuitesWithQuestionCount(
        eligibleQuestionIds: List<Long>,
    ): Result<List<GetSuitesWithQuestionCountByQuestionIds>> {
        return runCatching {
            db.suiteEntityQueries
                .getSuitesWithQuestionCountByQuestionIds(eligibleQuestionIds)
                .executeAsList()
        }
    }

    suspend fun loadQuestionFromSuite(suiteId: Long): Result<List<Question>> {
        return runCatching {
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
        return runCatching {
            db.historyEntityQueries
                .insertHistory(questionId, isCorrect, dateTimeUtils.getCurrentTimestamp())
        }
    }

    suspend fun getLearnedQuestionIds(
        eligibleQuestionIds: List<Long>
    ): Result<List<Long>> {
        return runCatching {
            db.historyEntityQueries
                .getLearnedQuestionIdsFilteredByIds(eligibleQuestionIds)
                .executeAsList()
        }
    }

    suspend fun countLearnedQuestions(
        suiteId: Long,
        eligibleQuestionIds: List<Long>
    ): Result<Long> {
        return runCatching {
            db.questionSuiteEntityQueries
                .countLearnedQuestions(suiteId, eligibleQuestionIds)
                .executeAsOne()
        }
    }

    suspend fun countVisitedQuestions(): Result<Long> {
        return kotlin.runCatching {
            db.historyEntityQueries.countVisitedQuestions().executeAsOne()
        }
    }

    fun getImageDir(category: @Category String): String {
        return ".assets/$category/images"
    }

    suspend fun getQuestionIdsByCategory(category: @Category String): Result<List<Long>> {
        return runCatching {
            db.questionEntityQueries
                .getQuestionIdsByCategory(category)
                .executeAsList()
        }
    }
}