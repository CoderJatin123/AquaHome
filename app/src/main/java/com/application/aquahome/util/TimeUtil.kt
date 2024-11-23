package com.application.aquahome.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtil {
    fun formatTimeToReadable(timeMillis: Long): String {
        val format = SimpleDateFormat("h:mm a", Locale.getDefault())
        return format.format(Date(timeMillis))
    }
}