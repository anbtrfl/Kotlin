import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

typealias ImageGenerator = (query: String) -> ByteArray

class ImageProcessor(
    private val parallelism: Int,
    private val requests: ReceiveChannel<String>,
    private val publications: SendChannel<Pair<String, ByteArray>>,
    private val generator: ImageGenerator,
) {
    private val cache = mutableSetOf<String>()

    fun run(scope: CoroutineScope) {
        val semaphore = Semaphore(parallelism)

        scope.launch {
            for (request in requests) {
                scope.launch {
                    semaphore.withPermit {
                        if (request !in cache) {
                            cache.add(request)
                            val image = generator(request)
                            publications.send(request to image)
                        }
                    }
                }
            }
        }
    }
}
