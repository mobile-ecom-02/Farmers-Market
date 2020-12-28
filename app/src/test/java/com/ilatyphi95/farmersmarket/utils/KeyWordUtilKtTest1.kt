package com.ilatyphi95.farmersmarket.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class KeyWordUtilKtTest {

    companion object {
        @JvmStatic
        fun keywordArguments() = Stream.of(
            Arguments.of(listOf("african", "giant", "africangiant"), "Giant African", "", KEYWORD_LIMIT),
            Arguments.of(listOf("african", "giant", "snail", "africangiant", "africansnail", "giantsnail", "africangiantsnail"), "Giant African Snail", "", KEYWORD_LIMIT),
            Arguments.of(listOf("african", "giant", "africangiant"), "Giant", "Giant African", KEYWORD_LIMIT),
            Arguments.of(listOf("african", "giant", "snail", "africangiant", "africansnail", "giantsnail", "africangiantsnail"), "Giant African Snail", "Giant African Snail", KEYWORD_LIMIT),
        )
    }

    @ParameterizedTest()
    @MethodSource("keywordArguments")
    fun `given input title and description, when generating keywords, then it should return valid keywords`(
        expected: List<String>,
        title: String,
        description: String,
        limit: Int
    ) {
        val actual = getKeywords(title, description, limit)

        assertEquals(expected.size, actual.size)
        assertEquals(expected, actual)

        assertTrue(actual.containsAll(expected), "All expected entries are present")

        val actualSet = actual.toSet()
        assertEquals(actualSet.size, actual.size, "Has no duplicate")


    }
}
