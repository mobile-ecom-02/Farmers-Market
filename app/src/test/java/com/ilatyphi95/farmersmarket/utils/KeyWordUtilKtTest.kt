package com.ilatyphi95.farmersmarket.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class CheckKeywordsTest(
    private val expected: List<String>,
    private val title: String,
    private val description: String,
    private val limit: Int = 10,
    private val scenerio: String
) {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    companion object {

        @JvmStatic
        @Parameterized.Parameters()
        fun todos() = listOf(
            arrayOf(listOf("basket", "mangoes"), "basket of mangoes", "", 10, "Confirm correct keyword are generated")
        )
    }

    @Test
    fun test_outputList() {
        val actual = getKeywords(title, description, limit)
        assertEquals(expected, actual)
    }
}