package skype.bot;

import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MonoTest {

	@Test
	public void shouldRetryCachedError() {
		SampleService sampleService = new SampleService();

		Mono<String> cachedMono = Mono.fromSupplier(sampleService::call).cache();
		Mono<String> producer = Mono.defer(() -> cachedMono);
		try {
			producer.block();
			fail();
		} catch (RuntimeException e) {
			System.out.println("Caught exception : " + e);
		}

		AtomicInteger i = new AtomicInteger(0);
		String result = producer.otherwise(e -> {
			if (i.get() == 3) {
				sampleService.serverAvailable = true;
			} else {
				i.set(i.get() + 1);
			}
			return Mono.just(sampleService.call());
		}).retry(5, t -> {
			System.out.println("THROWABLE " + t);
			System.out.println("ASKING FOR RETRY");
			return true;
		}).block();
		assertEquals("Success", result);
	}

	class SampleService {
		private boolean serverAvailable = false;

		String call() {
			System.out.println("Calling service with availability: " + serverAvailable);
			if (serverAvailable) {
				return "Success";
			} else {
				throw new RuntimeException("Error");
			}
		}
	}
}
