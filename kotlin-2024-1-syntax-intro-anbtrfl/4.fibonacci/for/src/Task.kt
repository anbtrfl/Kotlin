fun fibonacciFor(n: Int): Int {
    var f1 = 0
    var f2 = 1
    for (i in 0 until n) {
        val copy = f1
        f1 = f2
        f2 += copy
    }
    return f1
}
