package skype.bot.service

import org.springframework.http.*
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import skype.bot.entity.message.User

data class ConversationMessage(val text: String,
							   val from: User,
							   val recipient: User,
		//							   val replyToId: String,
							   val type: String = "message")

data class ConversationConfig(val serviceUrl: String,
							  val conversationId: String,
		//							  val activityId: String,
							  val bearerToken: String)

data class ConversationResponse(val id: String)

@Component
class ConnectorService {


	fun replyToConversation(msg: ConversationMessage, config: ConversationConfig): Mono<ConversationResponse>? {
//		val restTemplate = RestTemplate()
//		try {
//			val httpHeaders = HttpHeaders()
//			httpHeaders.set("Authorization", "Bearer ${config.bearerToken}")
//			val httpEntity = HttpEntity(msg, httpHeaders)
//			val resp: ResponseEntity<String> = restTemplate.exchange(
//					"${config.serviceUrl}/v3/conversations/${config.conversationId}/activities/${config.activityId}",
//					HttpMethod.POST,
//					httpEntity
//			)
//			println(resp.statusCode)
//			println(resp.statusCodeValue)
//			println(resp.headers)
//			println(resp.body)
//		} catch (e: Exception) {
//			e.printStackTrace()
//		}
//		return Mono.empty()

		val client = WebClient.create(ReactorClientHttpConnector())
		val clientRequest = ClientRequest.POST("${config.serviceUrl}/v3/conversations/${config.conversationId}/activities/")
				.header("Authorization", "Bearer ${config.bearerToken}")
				.body(fromObject(msg))
		println("CLIENT_REQUEST" + clientRequest)
		return client.exchange(clientRequest).log()
//				.doOnNext { resp ->
//					println("::::::RESPONSE " + resp.statusCode() + " :: ")
//					resp.headers().asHttpHeaders().forEach { (k, v) -> println("header  $k=$v") }
//					println(resp.bodyToMono(String::class).block())
//				}
				.doOnError { e -> println("REQUEST ERROR :" + e.localizedMessage + " :: ") }
				.doOnSubscribe { println("Subscribed $it") }
				.then { resp ->
					println("::::::RESPONSE " + resp.statusCode() + " :: ")
					resp.headers().asHttpHeaders().forEach { (k, v) -> println("header  $k=$v") }
					resp.bodyToMono(ConversationResponse::class)
				}

	}

}

