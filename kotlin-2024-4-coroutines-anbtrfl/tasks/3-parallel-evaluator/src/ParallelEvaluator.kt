import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*

class ParallelEvaluator {
    suspend fun run(
        task: Task,
        n: Int,
        context: CoroutineContext,
    ) {
        val scope = CoroutineScope(context)

        val asyncJobs = (0 until n).map { i ->
            scope.async {
                try {
                    task.run(i)
                } catch (e: Exception) {
                    throw TaskEvaluationException(e)
                }
            }
        }

        try {
            asyncJobs.forEach { it.await() }
        } catch (e: Exception) {
            throw TaskEvaluationException(e)
        }
    }
}
