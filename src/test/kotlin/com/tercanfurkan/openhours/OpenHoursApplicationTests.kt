package com.tercanfurkan.openhours

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

	@Test
	fun `post open hours`() {
		val requestBody = """
			{
			   "monday":[],
			   "tuesday":[
				  {
					 "type":"open",
					 "value":36000
				  },
				  {
					 "type":"close",
					 "value":64800
				  }
			   ],
			   "wednesday":[],
			   "thursday":[
				  {
					 "type":"open",
					 "value":36000
				  },
				  {
					 "type":"close",
					 "value":64800
				  }
			   ],
			   "friday":[
				  {
					 "type":"open",
					 "value":36000
				  }
			   ],
			   "saturday":[
				  {
					 "type":"close",
					 "value":3600
				  },
				  {
					 "type":"open",
					 "value":36000
				  }
			   ],
			   "sunday":[
				  {
					 "type":"close",
					 "value":3600
				  },
				  {
					 "type":"open",
					 "value":43200
				  },
				  {
					 "type":"close",
					 "value":75600
				  }
			   ]
			}
        """.trimIndent()

		client.post().uri("/openhours")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(requestBody))
			.exchange()
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

}
