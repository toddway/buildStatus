package core.usecase

import core.entity.BuildConfig
import core.entity.BuildStatus
import core.entity.Stats
import core.toDocumentList

interface GetSummaryUseCase {
    fun value() : String?
    fun key() : String
    fun isSuccessful() : Boolean
    companion object
}

fun List<GetSummaryUseCase>.postAll(postStatusUseCase: PostStatusUseCase) {
    forEach { summary ->
        summary.value()?.let {
            val result = if (summary.isSuccessful()) BuildStatus.SUCCESS else BuildStatus.FAILURE
            postStatusUseCase.post(result, it, summary.key())
        }
    }
}

fun BuildConfig.buildGetSummaryUseCases(): List<GetSummaryUseCase> {
    return listOf(
            GetDurationSummaryUseCase(this),
            GetCoverageSummaryUseCase(
                    coberturaReports.toDocumentList(),
                    CreateCoverageCoberturaMap(),
                    minCoveragePercent),
            GetCoverageSummaryUseCase(
                    jacocoReports.toDocumentList(),
                    CreateCoverageJacocoMap(),
                    minCoveragePercent),
            GetLintSummaryUseCase(
                    androidLintReports.toDocumentList()
                            + checkstyleReports.toDocumentList()
                            + cpdReports.toDocumentList(),
                    maxLintViolations
            )
    )
}

fun BuildConfig.stats(summaries : List<GetSummaryUseCase>) : Stats {
    val lintUseCase = summaries.find { it is GetLintSummaryUseCase } as GetLintSummaryUseCase
    val coverageUseCase = summaries.find { it is GetCoverageSummaryUseCase } as GetCoverageSummaryUseCase
    return Stats(
            coverageUseCase.percent() ?: 0.0,
            lintUseCase.asTotal() ?: 0,
            duration(),
            coverageUseCase.lines() ?: 0,
            git.commitDate,
            git.commitHash,
            git.commitBranch
    )
}
