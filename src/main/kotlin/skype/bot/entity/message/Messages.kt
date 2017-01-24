package skype.bot.entity.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
		Type(value = ConversationUpdateMessage::class, name = "conversationUpdate"),
		Type(value = TextMessage::class, name = "message"),
		Type(value = PingMessage::class, name = "ping")
)
sealed class ChatMessage(val type: String) {
	lateinit var conversation: Conversation
	lateinit var serviceUrl: String
	lateinit var from: User
	lateinit var recipient: User
	lateinit var id: String
}

data class ConversationUpdateMessage(
		val membersAdded: List<User>
) : ChatMessage("conversationUpdate")

data class TextMessage(
		val text: String,
		val channelData: ChannelData?
) : ChatMessage("message")


class PingMessage : ChatMessage("ping")
