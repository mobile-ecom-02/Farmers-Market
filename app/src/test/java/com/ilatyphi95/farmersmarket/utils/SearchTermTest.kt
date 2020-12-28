package com.ilatyphi95.farmersmarket.utils

import org.junit.Test
import org.junit.jupiter.api.Assertions.*

internal class SearchTermTest {
    @Test
    fun previewTest() {
        val actual = searchTerm("Basket of Apples")
        val expected = "applesbasket"
        assertEquals(expected, actual)
    }
}
