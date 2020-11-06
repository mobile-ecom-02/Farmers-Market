package com.ilatyphi95.farmersmarket.utils

import android.content.Context
import com.ilatyphi95.farmersmarket.R
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

/**
 * @param timeStamp milliseconds passed since epoch
 * @return short format when its today, or long format for date other than today
 */
fun toDate(timeStamp: Long) : String {
    val beginOfToday = LocalDate.now(ZoneId.systemDefault()).atStartOfDay()
    val timeStampDate = LocalDateTime
        .ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault())

    return when {
        beginOfToday < timeStampDate -> {
            timeStampDate.format(DateTimeFormatter.ofPattern("hh:mma"))
        }
        else -> {
            timeStampDate.format(DateTimeFormatter.ofPattern("d/MMM/yyyy"))
        }
    }
}

fun toDate(context: Context, timeStamp: Long) : String {
    val beginOfToday = LocalDate.now(ZoneId.systemDefault()).atStartOfDay()
    val yesterday = beginOfToday.minusDays(1)
    val timeStampDate = LocalDateTime
        .ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault())

    val yesterdayString = context.getString(R.string.yesterday)

    return when {
        beginOfToday < timeStampDate -> {
            timeStampDate.format(DateTimeFormatter.ofPattern("hh:mma"))
        }

        yesterday < timeStampDate && timeStampDate < beginOfToday -> {
            yesterdayString
        }
        else -> {
            timeStampDate.format(DateTimeFormatter.ofPattern("d/MM/yyyy"))
        }
    }
}

fun toShortTime(timeStamp: Long) : String {
    val timeStampDate = LocalDateTime
        .ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault())

    return timeStampDate.format(DateTimeFormatter.ofPattern("hh:mma"))
}