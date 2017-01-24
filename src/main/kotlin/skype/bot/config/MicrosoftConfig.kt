package skype.bot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "microsoft.app")
class MicrosoftConfig {
	lateinit var id: String
	lateinit var password: String

	override fun toString(): String {
		return "MicrosoftConfig(id='$id', password='$password')"
	}

}

