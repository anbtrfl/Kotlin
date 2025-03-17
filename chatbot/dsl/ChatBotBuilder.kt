package chatbot.dsl

import chatbot.api.*
import chatbot.bot.Bot

@ChatBotDSL
class ChatBotBuilder(private val client: Client) {
    private var logLevel: LogLevel = LogLevel.ERROR
    private var behaviour: BehaviourBuilder = BehaviourBuilder()
    var contextManager: ChatContextsManager? = null

    fun use(logLevel: LogLevel) {
        this.logLevel = logLevel
    }

    fun use(contextManager: ChatContextsManager) {
        this.contextManager = contextManager
    }

    operator fun LogLevel.unaryPlus() {
        logLevel = this
    }

    fun build(): ChatBot {
        return Bot(
            logLevel,
            behaviour.handlers,
            contextManager,
            client,
        )
    }

    fun behaviour(init: BehaviourBuilder.() -> Unit) = behaviour.init()
}

fun chatBot(client: Client, init: ChatBotBuilder.() -> Unit): ChatBot {
    val builder = ChatBotBuilder(client)
    builder.init()
    return builder.build()
}
