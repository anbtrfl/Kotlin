import kotlin.time.Duration
import kotlinx.coroutines.flow.*

fun Flow<Cutoff>.resultsFlow(): Flow<Results> =
    runningFold(emptyMap<Int, Duration>()) { lastResults, cutoff ->
        lastResults + (cutoff.number to cutoff.time)
    }.drop(1).map { Results(it) }

fun Flow<Results>.scoreboard(): Flow<Scoreboard> {
    return map { results ->
        val sortedRows = results.results.entries
            .sortedBy { it.value }
            .mapIndexed { index, (number, time) ->
                ScoreboardRow(rank = index + 1, number = number, time = time)
            }
        Scoreboard(sortedRows)
    }
}
