package com.rdev.tt._utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeUtils {
    fun getCurrentTimestamp(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return currentDateTime.format(formatter)
    }
}