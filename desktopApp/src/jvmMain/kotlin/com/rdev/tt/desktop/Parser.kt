package com.rdev.tt.desktop

import com.rdev.tt.data.dataModule
import com.rdev.tt.data.database.AppDb
import com.rdev.tt.data.database.QuestionEntity
import com.rdev.tt.data.database.QuestionSuiteEntity
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatformTools
import java.io.File
import java.io.FileInputStream

@Serializable
private data class QuestionJson(
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
private data class TestSuiteJson(
    @SerialName("type")
    val name: String,
    @SerialName("questions")
    val questions: List<QuestionJson>
)

private suspend fun process(
    input: String,
    output: String,
    json: Json,
    scope: CoroutineScope,
    suiteStoreHelper: SuiteStoreHelper
): List<Job> {
    val inputDir = File(input)
    val outputDir = File(output)
    if (!inputDir.isDirectory || !outputDir.isDirectory) return emptyList()

    val jsonFiles = inputDir.listFiles { file -> file.name.endsWith("json") } ?: return emptyList()
    if (jsonFiles.isEmpty()) return emptyList()

    val jobs = mutableListOf<Job>()
    jsonFiles.forEach {
        jobs += parseAndSave(it, json, scope, suiteStoreHelper)
    }

    return jobs
}

@OptIn(ExperimentalSerializationApi::class)
private suspend fun parseAndSave(
    inputFile: File,
    json: Json,
    scope: CoroutineScope,
    suiteStoreHelper: SuiteStoreHelper
): List<Job> {
    val jobs = mutableListOf<Job>()

    withContext(Dispatchers.IO) {
        val suites = json.decodeFromStream<List<TestSuiteJson>>(FileInputStream(inputFile))
        suites.forEachIndexed { index, suite ->
            jobs += scope.async { suiteStoreHelper.storeSuite(suite, index) }
        }
    }

    return jobs
}

@Suppress("RedundantSuspendModifier")
private class SuiteStoreHelper(
    private val db: AppDb,
    private val categories: List<String>,
    private val genSuiteName: (String, Int) -> String
) {
    suspend fun storeSuite(suite: TestSuiteJson, suiteIndex: Int) {
        db.transaction {
            db.suiteEntityQueries.insertSuite(genSuiteName(suite.name, suiteIndex))
            val suiteId = db.suiteEntityQueries.getLastInsertedId().executeAsOne()

            suite.questions.asSequence().map {
                QuestionEntity(
                    id = -1,
                    question = it.question,
                    choices = listOf(it.firstChoice, it.secondChoice, it.thirdChoice),
                    answerIdx = it.answer,
                    explanation = it.explanation,
                    categories = categories,
                    image = it.image
                )
            }.forEach { entity ->
                db.questionEntityQueries.insertQuestion(
                    question = entity.question,
                    choices = entity.choices,
                    answerIdx = entity.answerIdx,
                    explanation = entity.explanation,
                    image = entity.image,
                    categories = categories
                )

                val questionId = db.questionEntityQueries.getLastInsertedId().executeAsOne()
                db.questionSuiteEntityQueries.insert(QuestionSuiteEntity(questionId, suiteId))
            }
        }
    }
}

fun main() = runBlocking {
    startKoin {
        modules(dataModule)
    }

    val db = KoinPlatformTools.defaultContext().get().get<AppDb>()
    val json = KoinPlatformTools.defaultContext().get().get<Json>()

    val suiteStoreHelper = SuiteStoreHelper(
        db =  db,
        categories = listOf("ftt", "practice"),
        genSuiteName = { name, index ->
            name
        }
    )
    val jobs = parseAndSave(
        File(".assets/input/ftt/practice.json"), json, this, suiteStoreHelper
    )

    jobs.joinAll()
}