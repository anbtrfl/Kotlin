package chatbot.dsl

import chatbot.api.*
import chatbot.bot.MessageProcessorContext

@ChatBotDSL
class MessageBuilder(val message: Message) {
    var text: String = ""
    var keyboard: Keyboard? = null
    var replyTo: MessageId? = null

    fun removeKeyboard() {
        keyboard = Keyboard.Remove
    }

    fun withKeyboard(init: KeyboardBuilder.() -> Unit) {
        val builder = KeyboardBuilder()
        builder.init()
        keyboard = Keyboard.Markup(builder.oneTime, builder.keyboard)
    }

    fun keyboardIsEmpty(): Boolean {
        return when (keyboard) {
            is Keyboard.Markup -> (keyboard as Keyboard.Markup).keyboard.all { row ->
                row.all { button -> button.text.isEmpty() }
            }

            else -> false
        }
    }
}

fun <C : ChatContext?> MessageProcessorContext<C>.sendMessage(
    chatId: ChatId,
    init: MessageBuilder.() -> Unit,
) {
    val builder = MessageBuilder(message)
    builder.init()

    if (builder.text.isEmpty() && builder.keyboard == null) return
    if (builder.keyboardIsEmpty()) return

    this.client.sendMessage(chatId, builder.text, builder.keyboard, builder.replyTo)
}

fun <C : ChatContext?> MessageProcessorContext<C>.sendMessage(
    chatId: ChatId,
    message: String,
) {
    if (message.isEmpty()) return

    this.client.sendMessage(chatId, message)
}
