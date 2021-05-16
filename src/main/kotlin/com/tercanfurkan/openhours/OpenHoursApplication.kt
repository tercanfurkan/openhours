package com.tercanfurkan.openhours

import com.tercanfurkan.openhours.Constants.Companion.OPEN_HOURS_URI
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.function.server.*
import javax.validation.ConstraintViolationException
import javax.validation.Valid


class Constants {
	companion object {
		const val OPEN_HOURS_URI = "/openhours"
	}
}

@SpringBootApplication
class OpenHoursApplication

fun main(args: Array<String>) {
	runApplication<OpenHoursApplication>(*args)
}

@Configuration
class RouterConfiguration {
	@Bean
	fun routes(postHandler: PostHandler) = coRouter {
		(accept(APPLICATION_JSON) and OPEN_HOURS_URI).nest {
			POST("", postHandler::handle)
		}

	}
}

data class OpeningHourX(val day: Day, val events: List<Event>) : HashMap<Day, List<Event>>()

@Component
class PostHandler() {
	suspend fun handle(@Valid @RequestBody req: ServerRequest): ServerResponse {
		val body = req.awaitBody<OpeningHoursMap>()
		return ServerResponse.ok()
			.contentType(MediaType.TEXT_PLAIN)
			.bodyValueAndAwait(body.toFormattedString())
	}
}
