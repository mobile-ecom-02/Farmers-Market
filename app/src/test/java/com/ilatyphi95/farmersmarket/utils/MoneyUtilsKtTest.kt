package com.ilatyphi95.farmersmarket.utils

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class MoneyUtilsKtTest {

    @Test
    fun stringToMoney_shouldGiveDefaultValueWhenInputInvalid() {
        //given
        val moneyString1 = ""
        val moneyString2 = "USD"
        val moneyString3 = "1.1"

        //when
        val money1 = stringToMoney(moneyString1)
        val money2 = stringToMoney(moneyString2)
        val money3 = stringToMoney(moneyString3)

        //then
        assertEquals(Money.zero(CurrencyUnit.of(Locale.getDefault())), money1)
        assertEquals(Money.zero(CurrencyUnit.of(Locale.getDefault())), money2)
        assertEquals(Money.zero(CurrencyUnit.of(Locale.getDefault())), money3)
    }

    @Test
    fun stringToMoney_shouldParseStringInputCorrectly() {
        //given
        val amount = 1.03
        val currencyCode = "USD"

        //when
        val money = stringToMoney("$currencyCode-$amount")

        //then
        assertEquals(amount.toBigDecimal(), money.amount)
        assertEquals(currencyCode, money.currencyUnit.code)
    }

    @Test
    fun moneyString_shouldParseCorrectly() {
        //given
        val amount = 1.03
        val currencyCode = "USD"
        val money = Money.of(CurrencyUnit.of(currencyCode), amount)

        //when
        val moneyString = money.moneyString()

        //then
        assertEquals("$currencyCode-$amount", moneyString)
    }
}