package unit

import core.toDocument
import core.usecase.*
import org.amshove.kluent.shouldBe
import org.junit.Assert
import org.junit.Test
import java.io.File

class GetSummaryUseCaseTests {

    @Test
    fun`when there are valid report documents, summaries are generated`() {
        val summaries : List<GetSummaryUseCase> = listOf(
                GetLintSummaryUseCase(listOf(File("./src/test/testFiles/lint-results-prodRelease.xml").toDocument())),
                GetJacocoSummaryUseCase(listOf(File("./src/test/testFiles/coverage.xml").toDocument())),
                GetCoberturaSummaryUseCase(listOf(File("./src/test/testFiles/cobertura-coverage.xml").toDocument())),
                GetCheckstyleSummaryUseCase(listOf(File("./src/test/testFiles/detekt-checkstyle.xml").toDocument())),
                GetTextSummaryUseCase(File("./src/test/testFiles/hello.txt"))
        )

        summaries.forEach {
            it.keyString().isNotBlank() shouldBe true
            Assert.assertTrue("${it.javaClass} summary was null or blank", !it.summaryString().isNullOrBlank())
        }
    }

    @Test
    fun`when there are invalid report documents, summaries are generated`() {
        val summaries : List<GetSummaryUseCase> = listOf(
                GetLintSummaryUseCase(listOf(File("./src/test/testFiles/coverage.xml").toDocument())),
                GetJacocoSummaryUseCase(listOf(File("./src/test/testFiles/cobertura-coverage.xml").toDocument())),
                GetCoberturaSummaryUseCase(listOf(File("./src/test/testFiles/coverage.xml").toDocument())),
                GetCheckstyleSummaryUseCase(listOf(File("./src/test/testFiles/coverage.xml").toDocument())),
                GetTextSummaryUseCase(File("./src/test/testFiles/coverage.xml"))
        )

        summaries.forEach {
            it.keyString().isNotBlank() shouldBe true
            Assert.assertTrue("${it.javaClass} summary was null or blank", !it.summaryString().isNullOrBlank())
        }
    }
}