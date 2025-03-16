interface Value<T> {
    var value: T
    fun observe(observer: (T) -> Unit): Cancellation
}

fun interface Cancellation {
    fun cancel()
}

class MutableValue<T>(initial: T) : Value<T> {
    private val observers: MutableSet<(T) -> Unit> = mutableSetOf()

    override var value: T = initial
        set(newValue) {
            field = newValue
            notifyObservers(newValue)
        }

    override fun observe(observer: (T) -> Unit): Cancellation {
        observers.add(observer)
        observer(value)
        return Cancellation {
            observers.remove(observer)
        }
    }

    private fun notifyObservers(newValue: T) {
        observers.forEach { it(newValue) }
    }
}
