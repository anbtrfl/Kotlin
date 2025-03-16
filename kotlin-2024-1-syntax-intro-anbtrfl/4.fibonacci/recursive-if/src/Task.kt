fun fibonacciIf(n: Int): Int {
    if (n == 0) {
        return 0
    }
    return if (n == 1) {
        1
    } else {
        fibonacciIf(n - 1) + fibonacciIf(n - 2)
    }
}
