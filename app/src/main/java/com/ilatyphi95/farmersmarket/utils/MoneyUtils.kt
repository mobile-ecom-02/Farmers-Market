package com.ilatyphi95.farmersmarket.utils

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.util.*

/**
 * @param moneyString combination of CurrencyCode and Number separated by '-'
 * @return Joda Money for valid input or ZeroMoney of current System Locale
 * @throws java.lang.ArithmeticException org.joda.money.IllegalCurrencyException
 */
fun stringToMoney(moneyString: String) : Money {
    val moneyParts = moneyString.split("-")
    return if(moneyParts.size != 2) {
        Money.zero(CurrencyUnit.of(Locale.getDefault()))
    } else {
        Money.of(CurrencyUnit.of(moneyParts[0]), moneyParts[1].toDouble())
    }
}

/**
 * @receiver instance of Joda Money
 * @return combination of currencyCode and Number separated by '-'
 */
fun Money.moneyString() : String {
    return "${this.currencyUnit.code}-${this.amount}"
}