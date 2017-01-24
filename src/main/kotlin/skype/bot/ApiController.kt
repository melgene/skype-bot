package skype.bot

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import skype.bot.entity.message.ChatMessage
import skype.bot.entity.message.ConversationUpdateMessage
import skype.bot.entity.message.PingMessage
import skype.bot.entity.message.TextMessage
import skype.bot.service.AuthService
import skype.bot.service.ConnectorService
import skype.bot.service.ConversationConfig
import skype.bot.service.ConversationMessage

@RestController
@RequestMapping("/api")
class ApiController(val authService: AuthService, val connectorService: ConnectorService) {

	@PostMapping("/messages")
	@ResponseStatus(HttpStatus.ACCEPTED)
	fun messages(@RequestBody message: Mono<ChatMessage>): Mono<Void> {
		println("GOT TO ENDPOINT")
		return message.doOnNext { handleMessage(it) }
				.doOnError { e -> println("Error occurred" + e) }
				.then()
	}

	private fun handleMessage(msg: ChatMessage) {
		when (msg) {
			is PingMessage -> {
				println("CHECKING HEALTH")

			}
			is ConversationUpdateMessage -> {
				println(msg)

			}
			is TextMessage -> handleTextMessage(msg).subscribeOn(Schedulers.parallel()).subscribe()
		}
	}

	private fun handleTextMessage(msg: TextMessage): Mono<Void> {
		return authService.authenticate().flatMap { authResponse ->
			val response = ConversationMessage("You sent message: '${msg.text}'",
					from = msg.recipient,
					recipient = msg.from
//					replyToId = msg.id
			)
			val config = ConversationConfig(
					serviceUrl = msg.serviceUrl,
					conversationId = msg.conversation.id,
					//					activityId = msg.channelData.clientActivityId,
					bearerToken = authResponse.accessToken)
			println("SENDING :" + authResponse)
			println("RESPONDING TO :" + msg)
			println("url: " + config.serviceUrl)
			connectorService.replyToConversation(response, config)
		}.then()
	}

}


