import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*

fun CoroutineScope.runApplication(
    runUI: suspend () -> Unit,
    runApi: suspend () -> Unit,
) {
    val api = launch {
        while (true) {
            try {
                runApi()
                break
            } catch (e: CancellationException) {
                break
            } catch (e: Exception) {
                delay(1.seconds)
            }
        }
    }

    launch {
        try {
            runUI()
        } catch (e: Exception) {
            api.cancel()
            throw e
        }
    }
}
