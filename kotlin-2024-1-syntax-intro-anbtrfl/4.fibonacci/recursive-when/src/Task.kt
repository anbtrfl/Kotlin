fun fibonacciWhen(n: Int): Int {
    when (n) {
        0 -> return 0
        1 -> return 1
        else -> return fibonacciWhen(n - 1) + fibonacciWhen(n - 2)
    }
}
