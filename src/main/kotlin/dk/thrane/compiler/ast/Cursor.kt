package dk.thrane.compiler.ast

class Cursor(val source: String) {
    private var idx: Int = 0
    var lineNumber: Int = 0
        private set

    val remainingString: String
        get() = source.substring(idx)

    fun advance(count: Int) {
        lineNumber += source.substring(idx, idx + count).map { if (it == '\n') 1 else 0 }.sum()
        idx += count
    }

    fun withdraw(count: Int) {
        lineNumber -= source.substring(idx - count, idx).map { if (it == '\n') 1 else 0 }.sum()
        idx -= count
    }
}