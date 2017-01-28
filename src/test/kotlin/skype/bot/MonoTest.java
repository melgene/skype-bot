package skype.bot;

import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MonoTest {

	@Test
	public void shouldRetryCachedError() {
		SampleService sampleService = new SampleService();
		Mono<String> producer = Mono.fromSupplier(sampleService::call).cache();
		try {
			producer.block();
			fail();
		} catch (RuntimeException e) {
			System.out.println("Caught exception : " + e);
		}

		sampleService.serverAvailable = true;
		String result = producer.retry(1, t -> {
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
