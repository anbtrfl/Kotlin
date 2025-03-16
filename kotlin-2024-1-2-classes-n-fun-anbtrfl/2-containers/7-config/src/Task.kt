import java.io.InputStream
import java.io.InputStreamReader
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Config(fileName: String) {

    private val map: Map<String, String> = extractContent(fileName)

    init {
        require(map.isNotEmpty()) { "config file is invalid" }
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, String> {
        if (!map.containsKey(property.name)) {
            throw IllegalArgumentException("not found")
        }
        return ReadOnlyProperty { _, _ -> map[property.name]!! }
    }

    companion object {
        private fun extractContent(fileName: String): Map<String, String> {
            val inputStream = getResource(fileName)
            requireNotNull(inputStream) { "file not found" }

            val resultMap = mutableMapOf<String, String>()
            InputStreamReader(inputStream).forEachLine { line ->
                val (key, value) = line.split("=").map { it.trim() }
                require(key.isNotEmpty() && value.isNotEmpty()) { "invalid config line" }
                resultMap[key] = value
            }

            return resultMap
        }
    }
}

@Suppress(
    "RedundantNullableReturnType",
    "UNUSED_PARAMETER",
)
fun getResource(fileName: String): InputStream? {
    // do not touch this function
    val content =
        """
        |valueKey = 10
        |otherValueKey = stringValue 
        """.trimMargin()

    return content.byteInputStream()
}
