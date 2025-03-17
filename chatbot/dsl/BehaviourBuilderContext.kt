package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.Message
import chatbot.bot.MessageHandler
import chatbot.bot.MessageProcessor
import chatbot.bot.MessageProcessorContext

@ChatBotDSL
open class BehaviourBuilderContext<C : ChatContext?> {
    val handlers = mutableListOf<MessageHandler<C>>()

    fun onCommand(command: String, handle: MessageProcessor<C>) {
        onMessagePrefix("/$command", handle)
    }

    fun onMessage(predicate: (Message) -> Boolean, handle: MessageProcessor<C>) {
        handlers.add(MessageHandler({ message, _ -> predicate(message) }, handle))
    }

    fun onMessagePrefix(prefix: String, handle: MessageProcessor<C>) {
        onMessage({ it.text.startsWith(prefix) }, handle)
    }

    fun onMessageContains(text: String, handle: MessageProcessor<C>) {
        onMessage({ it.text.contains(text) }, handle)
    }

    fun onMessage(messageTextExactly: String, handle: MessageProcessor<C>) {
        onMessage({ it.text == messageTextExactly }, handle)
    }

    fun onMessage(action: MessageProcessor<C>) {
        onMessage({ true }, action)
    }
}

class BehaviourBuilder : BehaviourBuilderContext<ChatContext?>() {
    inline fun <reified T : ChatContext?> into(init: BehaviourBuilderContext<T>.() -> Unit) {
        handlers.addAll(
            BehaviourBuilderContext<T>().apply(init).handlers.map {
                MessageHandler(
                    predicate = { message, context -> context is T && it.predicate(message, context) },
                    processor = {
                        require(context is T) { "Processor's call without checking the predicate" }
                        val context = MessageProcessorContext(message, client, context as T, setContext)
                        it.processor(context)
                    },
                )
            },
        )
    }

    inline infix fun <reified T : ChatContext?> T.into(init: BehaviourBuilderContext<T>.() -> Unit) {
        handlers.addAll(
            BehaviourBuilderContext<T>().apply(init).handlers.map {
                MessageHandler(
                    predicate = { message, context ->
                        this == context && context is T && it.predicate(message, context)
                    },
                    processor = {
                        require(context is T) { "Processor's call without checking the predicate" }
                        val context = MessageProcessorContext(message, client, context as T, setContext)
                        it.processor(context)
                    },
                )
            },
        )
    }
}
