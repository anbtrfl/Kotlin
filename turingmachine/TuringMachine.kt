package turingmachine

class TuringMachine(
    private val startingState: String,
    private val acceptedState: String,
    private val rejectedState: String,
    private val transitions: Collection<TransitionFunction>,
) {
    class Snapshot(var state: String, val tape: Tape) {

        fun applyTransition(transition: Transition): Snapshot {
            val newTape = tape.applyTransition(transition.newSymbol, transition.move)
            return Snapshot(transition.newState, newTape)
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Snapshot) return false
            return state == other.state && tape == other.tape
        }

        override fun toString(): String = "State: $state\n$tape"

        fun copy(): Snapshot = Snapshot(state, tape.copy())
        override fun hashCode(): Int {
            var result = state.hashCode()
            result = 31 * result + tape.hashCode()
            return result
        }
    }

    class Tape(input: String, initialPosition: Int = 0) {
        val tape: ArrayDeque<Char> = ArrayDeque(input.toList())
        var headPosition: Int = 0

        init {
            headPosition = initialPosition
        }

        constructor(tape: ArrayDeque<Char>, headPosition: Int) : this("") {
            this.tape.clear()
            this.tape.addAll(tape)
            this.headPosition = headPosition
        }

        val content: CharArray
            get() {
                val firstIndex =
                    minOf(tape.indexOfFirst { it != BLANK }.takeIf { it != -1 } ?: headPosition, headPosition)
                val lastIndex =
                    maxOf(tape.indexOfLast { it != BLANK }.takeIf { it != -1 } ?: headPosition, headPosition)

                return (firstIndex..lastIndex).map { idx ->
                    tape.getOrNull(idx) ?: BLANK
                }.toCharArray()
            }

        val position: Int
            get() = headPosition - minOf(
                tape.indexOfFirst { it != BLANK }.takeIf { it != -1 } ?: headPosition,
                headPosition,
            )

        fun applyTransition(char: Char, move: TapeTransition): Tape {
            adjustTape()

            tape[headPosition] = char
            when (move) {
                TapeTransition.Left -> headPosition--
                TapeTransition.Right -> headPosition++
                TapeTransition.Stay -> {}
            }

            return this
        }

        private fun adjustTape() {
            when {
                headPosition < 0 -> {
                    tape.addFirst(BLANK)
                    headPosition = 0
                }
                headPosition >= tape.size -> tape.addLast(BLANK)
            }
        }

        fun copy(): Tape {
            val newTape = Tape(ArrayDeque(tape), headPosition)
            return newTape
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Tape) return false
            return content.contentEquals(other.content) && position == other.position
        }

        override fun toString(): String = buildString {
            content.forEachIndexed { index, c ->
                append(if (index == position) "[$c]" else "$c")
            }
        }

        override fun hashCode(): Int {
            var result = tape.hashCode()
            result = 31 * result + headPosition
            return result
        }
    }

    fun initialSnapshot(input: String): Snapshot = Snapshot(startingState, Tape(input))

    fun simulateStep(snapshot: Snapshot): Snapshot {
        val currentSymbol =
            if (snapshot.tape.headPosition < 0 || snapshot.tape.headPosition >= snapshot.tape.tape.size) {
                BLANK
            } else {
                snapshot.tape.tape[snapshot.tape.headPosition]
            }

        val transition = transitions.find { it.state == snapshot.state && it.symbol == currentSymbol }

        return if (transition != null) {
            snapshot.copy().applyTransition(transition.transition)
        } else {
            Snapshot(rejectedState, snapshot.tape.copy())
        }
    }

    fun simulate(input: String): Sequence<Snapshot> = sequence {
        var snapshot = initialSnapshot(input)
        yield(snapshot)
        while (snapshot.state != acceptedState && snapshot.state != rejectedState) {
            snapshot = simulateStep(snapshot)
            yield(snapshot)
        }
    }
}
