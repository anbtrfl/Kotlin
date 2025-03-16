fun greet(name: String): String {
    return "Hello, $name!"
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        val name = readlnOrNull()
        if (name.isNullOrBlank()) {
            println(greet("Anonymous"))
        } else {
            println(greet(name))
        }
    } else {
        for (element in args) {
            println(greet(element))
        }
    }
}
