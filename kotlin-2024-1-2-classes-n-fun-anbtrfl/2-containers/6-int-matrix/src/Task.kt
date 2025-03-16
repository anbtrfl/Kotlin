class IntMatrix(val rows: Int, val columns: Int) {
    private val data: IntArray

    init {
        require(rows > 0) { "rows must be >0" }
        require(columns > 0) { "columns must be >0" }
        this.data = IntArray(rows * columns)
    }

    operator fun get(row: Int, col: Int): Int {
        return data[index(row, col)]
    }

    operator fun set(row: Int, col: Int, value: Int) {
        data[index(row, col)] = value
    }

    private fun index(row: Int, col: Int): Int {
        require(row in 0 until rows) { "row index is incorrect. out of bounds" }
        require(col in 0 until columns) { "column index is incorrect. out of bounds" }
        return row * columns + col
    }
}

fun main() {
    val matrix = IntMatrix(3, 4)
    println(matrix.rows)
    println(matrix.columns)
    println(matrix[0, 0])
    matrix[2, 3] = 42
    println(matrix[2, 3])
}
