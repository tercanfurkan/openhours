package com.tercanfurkan.openhours

import com.tercanfurkan.openhours.Constants.Companion.OPEN_HOURS_URI
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

@SpringBootTest(classes = [OpenHoursApplication::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OpenHoursApplicationTests {

	@Autowired
	private lateinit var routerFunction: RouterFunction<ServerResponse>

	private lateinit var client: WebTestClient

	@BeforeAll
	fun setup() {
		client = WebTestClient.bindToRouterFunction(routerFunction).build()
	}

	private fun postJsonRequest(jsonFile: String): WebTestClient.ResponseSpec{
		val requestBody = this::class.java.classLoader.getResource(jsonFile).readText()
		return client.post().uri(OPEN_HOURS_URI)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(requestBody))
			.exchange()
	}

	@Test
	fun `complete test`() {
		postJsonRequest("complete_specs.json")
			.expectStatus().isOk
			.expectBody<String>().isEqualTo("""
				Monday: Closed
				Tuesday: 10 AM - 6 PM
				Wednesday: Closed
				Thursday: 10 AM - 6 PM
				Friday: 10 AM - 1 AM
				Saturday: 10 AM - 1 AM
				Sunday: 12 PM - 9 PM
			""".trimIndent())
	}

	@Test
	fun `marks empty days as closed`() {
		postJsonRequest("empty_days.json")
			.expectStatus().isOk
			.expectBody<String>().isEqualTo("""
				Monday: Closed
				Tuesday: Closed
				Wednesday: Closed
				Thursday: Closed
				Friday: Closed
				Saturday: Closed
				Sunday: Closed
			""".trimIndent())
	}

	@Test
	fun `marks missing days as closed`() {
		postJsonRequest("missing_days.json")
			.expectStatus().isOk
			.expectBody<String>().isEqualTo("""
				Monday: Closed
				Tuesday: Closed
				Wednesday: Closed
				Thursday: Closed
				Friday: Closed
				Saturday: Closed
				Sunday: Closed
			""".trimIndent())
	}

	@Test
	fun `handles multiple open-close events per day`() {
		postJsonRequest("multiple_events_per_day.json")
			.expectStatus().isOk
			.expectBody<String>().isEqualTo("""
				Monday: Closed
				Tuesday: Closed
				Wednesday: Closed
				Thursday: Closed
				Friday: Closed
				Saturday: 9 AM - 11 AM, 4 PM - 6 PM, 8 PM - 9 PM
				Sunday: Closed
			""".trimIndent())
	}

	@Test
	fun `handles when not closed during the same day`() {
		postJsonRequest("not_closed_during_the_same_day.json")
			.expectStatus().isOk
			.expectBody<String>().isEqualTo("""
				Monday: Closed
				Tuesday: Closed
				Wednesday: Closed
				Thursday: Closed
				Friday: 6 PM - 1 AM
				Saturday: 9 AM - 11 AM
				Sunday: Closed
			""".trimIndent())
	}
}
