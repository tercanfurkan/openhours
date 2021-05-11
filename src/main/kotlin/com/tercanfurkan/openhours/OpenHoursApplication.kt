package com.tercanfurkan.openhours

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@SpringBootApplication
class OpenHoursApplication

fun main(args: Array<String>) {
	runApplication<OpenHoursApplication>(*args)
}

@Configuration
class RouterConfiguration {
	@Bean
	fun routes(postHandler: PostHandler) = coRouter {
		(accept(APPLICATION_JSON) and "/openhours").nest {
			POST("", postHandler::handle)
		}

	}
}

@Component
class PostHandler() {
	suspend fun handle(req: ServerRequest): ServerResponse {
		val body = req.awaitBody<Map<Day, List<Event>>>()
		return ServerResponse.ok()
			.contentType(MediaType.TEXT_PLAIN)
			.bodyValueAndAwait(body.toFormattedString())
	}
}
