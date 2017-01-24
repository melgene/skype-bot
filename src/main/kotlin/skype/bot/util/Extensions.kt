package skype.bot.util

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

fun <K, V> multiValueMapOf(vararg pairs: kotlin.Pair<K, V>): MultiValueMap<K, V> {
	val map = LinkedMultiValueMap<K, V>(pairs.size)
	for ((key, value) in pairs) {
		map.add(key, value)
	}
	return map
}

