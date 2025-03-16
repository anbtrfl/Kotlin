fun sendMessageToClient(
    client: Client?,
    message: String?,
    mailer: Mailer,
) {
    val email: String = client?.personalInfo?.email ?: return
    mailer.sendMessage(email, message ?: "Hello!")
}
