package com.ilatyphi95.farmersmarket.utils

import android.content.Context
import com.google.firebase.Timestamp
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.Product
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

/**
 * @param timeStamp milliseconds passed since epoch
 * @return short format when its today, or long format for date other than today
 */
fun toDate(timeStamp: Timestamp) : String {
    val beginOfToday = LocalDate.now(ZoneId.systemDefault()).atStartOfDay()
    val timeStampDate = timeStamp.toLocalDateTime()

    return when {
        beginOfToday < timeStampDate -> {
            timeStampDate.format(DateTimeFormatter.ofPattern("hh:mma"))
        }
        else -> {
            timeStampDate.format(DateTimeFormatter.ofPattern("d/MMM/yyyy"))
        }
    }
}

fun Timestamp.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime = LocalDateTime
    .ofInstant(Instant.ofEpochMilli(seconds * 1000 + nanoseconds/1000000), zone)

fun LocalDateTime.toTimeStamp(): Timestamp =
    Timestamp(atZone(ZoneId.systemDefault()).toEpochSecond(), nano)

fun toDate(context: Context, timeStamp: Timestamp) : String {
    val beginOfToday = LocalDate.now(ZoneId.systemDefault()).atStartOfDay()
    val yesterday = beginOfToday.minusDays(1)
    val timeStampDate = timeStamp.toLocalDateTime()

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

fun longToLocalDateTime(long: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(long), ZoneId.systemDefault())

fun Product.toAdItem() = AdItem(itemId = this.id, name = this.name, price = this.priceStr,
    quantity = this.qtyAvailable, imageUrl = this.imgUrls.getOrElse(0){""})