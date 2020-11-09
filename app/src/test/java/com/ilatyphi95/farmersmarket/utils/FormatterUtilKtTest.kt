package com.ilatyphi95.farmersmarket.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class FormatterUtilKtTest {

    @Test
    fun toTime_shouldReturnShortDateForToday() {
        //given
        val systemZone = ZoneOffset.systemDefault()
        val now: LocalDateTime = LocalDate.now(systemZone).atTime(23, 59)
        loadTimeZone()

        //when
        val nowString = toDate(now.toTimeStamp())

        //then
        assertEquals("11:59PM", nowString)
    }

    @Test
    fun toTime_shouldReturnLongDateForEarlierThanToday() {
        //given
        val systemZone = ZoneOffset.systemDefault()
        val yesterday: LocalDateTime = LocalDate.now(systemZone).atTime(23,59).minusDays(1)
        loadTimeZone()

        //when
        val yesterdayString =
            toDate(yesterday.toTimeStamp())

        //then
        assertTrue(yesterdayString.contains("11:59PM"))
        assertTrue(yesterdayString.contains(yesterday.year.toString()))
        assertTrue(yesterdayString.contains(yesterday.dayOfMonth.toString()))
    }
}