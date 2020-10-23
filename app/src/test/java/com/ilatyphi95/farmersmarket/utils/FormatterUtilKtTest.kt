package com.ilatyphi95.farmersmarket.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class FormatterUtilKtTest {

    @Test
    fun toTime_shouldReturnShortDateForToday() {
        //given
        val systemZone = ZoneOffset.systemDefault()
        val now = LocalDate.now(systemZone)
        loadTimeZone()

        //when
        val nowString = toDate(now.atTime(23,59)
            .toInstant(systemZone.rules.getOffset(Instant.now())).toEpochMilli())

        //then
        assertEquals("11:59PM", nowString)
    }

    @Test
    fun toTime_shouldReturnLongDateForEarlierThanToday() {
        //given
        val systemZone = ZoneOffset.systemDefault()
        val yesterday = LocalDate.now(systemZone).atTime(23,59).minusDays(1)
        loadTimeZone()

        //when
        val yesterdayString =
            toDate(yesterday.toInstant(systemZone.rules.getOffset(Instant.now())).toEpochMilli())

        //then
        assertTrue(yesterdayString.contains("11:59PM"))
        assertTrue(yesterdayString.contains(yesterday.year.toString()))
        assertTrue(yesterdayString.contains(yesterday.dayOfMonth.toString()))
    }
}