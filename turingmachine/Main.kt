package turingmachine

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.double
import java.io.File

class TuringMachineApp : CliktCommand() {
    private val machineFile by argument("machineFile", help = "File describing the Turing Machine.")
    private val inputFile by argument("inputFile", help = "Input file for Turing Machine.").optional()
    private val auto by option("--auto", help = "Run automatically without pausing.").flag(default = false)
    private val delay by option("--delay", help = "Delay in seconds between steps.").double().default(0.5)

    override fun run() {
        val machineDescription = File(machineFile).readLines()
        val input = inputFile?.let { File(it).readText().trim() } ?: run {
            print("Enter input: ")
            readln()
        }

        val parsedMachine = parseMachine(machineDescription)
        val turingMachine = TuringMachine(
            startingState = parsedMachine.startingState,
            acceptedState = parsedMachine.acceptedState,
            rejectedState = parsedMachine.rejectedState,
            transitions = parsedMachine.transitions,
        )
        val sequence = turingMachine.simulate(input)

        for (snapshot in sequence) {
            println(snapshot)
            if (!auto) {
                readln()
            } else {
                Thread.sleep((delay * 1000).toLong())
            }

            if (snapshot.state == parsedMachine.acceptedState) {
                println("Accepted")
                break
            } else if (snapshot.state == parsedMachine.rejectedState) {
                println("Rejected")
                break
            }
        }
    }

    private fun parseMachine(lines: List<String>): ParsedMachine {
        val startingState = lines.first { it.startsWith("start:") }.split(":")[1].trim()
        val acceptedState = lines.first { it.startsWith("accept:") }.split(":")[1].trim()
        val rejectedState = lines.first { it.startsWith("reject:") }.split(":")[1].trim()
        val blank = lines.first { it.startsWith("blank:") }.split(":")[1].trim()[0]
        val transitions = lines.filter { it.contains("->") }.map {
            val (state, rest) = it.split(" ", limit = 2)
            val (symbol, _, newState, newSymbol, move) = rest.split(" ")
            TransitionFunction(
                state = state,
                symbol = symbol[0],
                transition = Transition(
                    newState = newState,
                    newSymbol = newSymbol[0],
                    move = when (move) {
                        ">" -> TapeTransition.Right
                        "<" -> TapeTransition.Left
                        "^" -> TapeTransition.Stay
                        else -> error("Invalid move: $move")
                    },
                ),
            )
        }
        return ParsedMachine(startingState, acceptedState, rejectedState, blank, transitions)
    }

    data class ParsedMachine(
        val startingState: String,
        val acceptedState: String,
        val rejectedState: String,
        val blank: Char,
        val transitions: List<TransitionFunction>,
    )
}

fun main(args: Array<String>) = TuringMachineApp().main(args)
