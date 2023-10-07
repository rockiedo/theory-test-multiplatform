package com.rdev.tt._utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DateTimeUtils {
    fun getCurrentTimestamp(): String {
        val currentMoment: Instant = Clock.System.now()
        val currentDateTime: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)
        return currentDateTime.toString()
    }
}