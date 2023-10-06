package com.rdev.tt.scripts

import com.rdev.tt.data.dataModule
import com.rdev.tt.data.database.AppDb
import com.rdev.tt.data.database.QuestionEntity
import com.rdev.tt.data.database.QuestionSuiteEntity
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatformTools
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

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

private suspend fun processDb(scope: CoroutineScope) {
    startKoin {
        modules(dataModule)
    }

    val db = KoinPlatformTools.defaultContext().get().get<AppDb>()
    val json = KoinPlatformTools.defaultContext().get().get<Json>()

    val suiteStoreHelper = SuiteStoreHelper(
        db = db,
        categories = listOf("ftt", "practice"),
        genSuiteName = { name, index ->
            name
        }
    )
    val jobs = parseAndSave(
        File(".assets/input/ftt/practice.json"), json, scope, suiteStoreHelper
    )

    jobs.joinAll()
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

@Suppress("RedundantSuspendModifier")
private suspend fun findUnusedAndNotFoundImages(
    requestedImages: Set<String>,
    imageDir: String
): ImageUsage {
    val file = File(imageDir)
    if (!file.exists() || !file.isDirectory) throw IllegalStateException("Invalid image dir")

    val incorrectFormatImages = mutableSetOf<String>()
    val unusedImages = mutableSetOf<String>()
    val usedImages = mutableSetOf<String>()

    file.listFiles()
        .asSequence()
        .map { it.name }
        .forEach { img ->
            val name = img.split(".").first()

            if (!img.endsWith(".webp")) {
                incorrectFormatImages.add(img)
            } else {
                if (name !in requestedImages) {
                    unusedImages.add(img)
                } else {
                    usedImages.add(name)
                }
            }
        }

    return ImageUsage(
        unusedImages = unusedImages.toList(),
        notFoundImages = requestedImages.subtract(usedImages).toList(),
        incorrectFormatImages = incorrectFormatImages.toList()
    )
}

@OptIn(ExperimentalSerializationApi::class)
private suspend fun unifyImages(
    inputPath: String,
    destPath: String,
    reportPath: String,
    json: Json
) {
    val inputDir = File(inputPath)
    val destDir = File(destPath)

    if (!inputDir.exists() || !destDir.exists()) {
        throw IllegalStateException("Directories do not exist.")
    }

    val duplicates = mutableListOf<String>()

    inputDir.listFiles()?.asSequence()?.forEach { file ->
            val destFile = File("$destPath/${file.name}")

            if (destFile.exists()) {
                duplicates.add(file.name)
            } else {
                file.copyTo(destFile)
            }
        }

    val reportFile = File(reportPath)
    withContext(Dispatchers.IO) {
        json.encodeToStream(duplicates, FileOutputStream(reportFile))
    }
}

private fun copyUsedImages(imageList: Set<String>, sourcePath: String, destPath: String) {
    val sourceDir = File(sourcePath)
    val destDir = File(destPath)

    if (!sourceDir.exists() || !destDir.exists()) {
        throw IllegalStateException("Directories do not exist.")
    }

    sourceDir.listFiles()?.filter { it.nameWithoutExtension in imageList }?.asSequence()?.forEach { file ->
        val destFile = File("$destPath/${file.name}")
        file.copyTo(destFile)
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun cliScript() = runBlocking {
    startKoin {
        modules(dataModule)
    }
    val json = KoinPlatformTools.defaultContext().get().get<Json>()

//    unifyImages(
//        ".assets/ftt/images",
//        ".assets/images",
//        ".assets/fileCopyReport.json",
//        json
//    )
//    return@runBlocking

    val inputFile = File(".assets/images.json")
    val requestedImages = withContext(Dispatchers.IO) {
        json.decodeFromStream<List<Image>>(FileInputStream(inputFile)).map { it.name }.toSet()
    }

    copyUsedImages(requestedImages, ".assets/all-images", ".assets/images")
    return@runBlocking

    val imageUsage = findUnusedAndNotFoundImages(requestedImages, ".assets/images")

    val outputFile = File(".assets/imageUsageReport.json")
    withContext(Dispatchers.IO) {
        json.encodeToStream(imageUsage, FileOutputStream(outputFile))
    }
}