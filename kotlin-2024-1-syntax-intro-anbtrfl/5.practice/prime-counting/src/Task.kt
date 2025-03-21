fun isPrime(n: Int): Boolean {
    if (n <= 1) return false
    var i = 2
    while (i * i <= n) {
        if (n % i == 0) {
            return false
        }
        i++
    }
    return true
}

fun piFunction(x: Double): Int {
    var cnt = 0
    for (i in 1..x.toInt()) {
        if (isPrime(i)) {
            cnt++
        }
    }
    return cnt
}
