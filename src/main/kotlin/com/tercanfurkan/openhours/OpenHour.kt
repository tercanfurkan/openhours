package com.tercanfurkan.openhours

import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

fun OpeningHoursMap.toFormattedString(): String {
    val (openings, closings) = this.flatMap { (day, events) -> events.map { event -> DayEventPair(day, event) } }
        .toSortedSet(compareBy(DayEventPair::day, DayEventPair::event))
        .partition { (_, event) -> event.type == "open" }

    val openHours = openings.map { (openingDay, openingEvent) -> // get all (open,close) pairs for each day
        val closingEvent = closings.getClosingEventFor(openingDay, openingEvent)
        OpenHour(openingDay, openingEvent.value, closingEvent.value)
    }

    val openHoursMap = openHours.groupBy { it.day }
    return EnumSet.allOf(Day::class.java) // iterate through each workday in case there are closed days
        .joinToString("\n") { day -> getFormattedDayString(day, openHoursMap[day]) }
}

fun List<DayEventPair>.getClosingEventFor(openingDay: Day, openingEvent: Event): Event {
    return (
            firstOrNull { (closingDay, closingEvent) ->
                openingDay == closingDay && closingEvent.value > openingEvent.value
            }
                ?: firstOrNull { (closingDay, closingEvent) -> closingDay > openingDay }
                ?: first() // loop back to the first closing event, otherwise throw error as the data is corrupted
            ).event
}

typealias OpeningHoursMap = Map<Day, List<Event>>

data class Event(val type: String, val value: Long) : Comparable<Event> {
    override fun compareTo(other: Event): Int {
        return value.compareTo(other.value)
    }
}

enum class Day(@JsonValue val day: String) {
    MONDAY("monday"),
    TUESDAY("tuesday"),
    WEDNESDAY("wednesday"),
    THURSDAY("thursday"),
    FRIDAY("friday"),
    SATURDAY("saturday"),
    SUNDAY("sunday")
}

data class OpenHour(val day: Day, val opening: Long, val closing: Long) {
    private val openingTime = opening.toTimeValue()
    private val closingTime = closing.toTimeValue()

    private fun Long.toTimeValue(): String{
        val time = LocalTime.ofSecondOfDay(this)
        return time.format(DateTimeFormatter.ofPattern("h a"))
    }
    fun toFormattedString(): String {
        return "$openingTime - $closingTime"
    }
}

data class DayEventPair(val day: Day, val event: Event)

fun getFormattedDayString(day: Day, openHours: List<OpenHour>?): String {
    val openHourFormattedString = openHours?.joinToString(", ") { it.toFormattedString() } ?: "Closed"
    return "${day.name.toLowerCase().capitalize()}: $openHourFormattedString"
}