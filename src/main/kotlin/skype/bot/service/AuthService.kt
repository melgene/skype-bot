package skype.bot.service

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromFormData
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import skype.bot.config.MicrosoftConfig
import skype.bot.util.multiValueMapOf

data class AuthResponse(@JsonProperty("access_token") val accessToken: String,
						@JsonProperty("token_type") val tokenType: String = "Bearer",
						@JsonProperty("expires_in") val expiresIn: Int = 3599,
						@JsonProperty("ext_expires_in") val extExpiresIn: Int = 0)

@Component
class AuthService(val microsoftConfig: MicrosoftConfig) {

	companion object {
		const val AUTH_ENDPOINT = "https://login.microsoftonline.com/botframework.com/oauth2/v2.0/token"
	}

	// This will cache error result for the ttl period
	// https://jira.spring.io/browse/SPR-14235
	@Cacheable("authCache")
	fun authenticate(): Mono<AuthResponse> {
		val client = WebClient.create(ReactorClientHttpConnector())
		val params = multiValueMapOf(
				"grant_type" to "client_credentials",
				"client_id" to microsoftConfig.id,
				"client_secret" to microsoftConfig.password,
				"scope" to "https://api.botframework.com/.default"
		)
		val clientRequest = ClientRequest.POST(AUTH_ENDPOINT)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(fromFormData(params))
		return client.exchange(clientRequest)
				.then { response -> response.bodyToMono(AuthResponse::class) }
				.cache()
	}

}

