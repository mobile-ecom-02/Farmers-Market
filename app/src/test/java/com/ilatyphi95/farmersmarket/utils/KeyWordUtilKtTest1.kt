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
//            Arguments.of(listOf("basket", "mangoes"), "Basket of Mangoes", "", 10),
//            Arguments.of(
//                listOf("basket", "mangoes", "pawpaws"),
//                "Basket of Mangoes and pawpaws",
//                "",
//                10
//            ),
//            Arguments.of(listOf("sunset"), " I am going to DESCRIBE a sunset!", "Sunset is the time of day when our sky meets the outer space solar winds. There are blue, pink, and purple swirls, spinning and twisting, like clouds of balloons caught in a whirlwind. The sun moves slowly to hide behind the line of horizon, while the moon races to take its place in prominence atop the night sky. People slow to a crawl, entranced, fully forgetting the deeds that must still be done. There is a coolness, a calmness, when the sun does set.", 10),
//            Arguments.of(listOf("apollo", "space", "mission"), "I am going to INFORM you about the Apollo 11 space mission. ", "On July 16, 1969, the Apollo 11 spacecraft launched from the Kennedy Space Center in Florida. Its mission was to go where no human being had gone before—the moon! The crew consisted of Neil Armstrong, Michael Collins, and Buzz Aldrin. The spacecraft landed on the moon in the Sea of Tranquility, a basaltic flood plain, on July 20, 1969. The moonwalk took place the following day. On July 21, 1969, at precisely 10:56 EDT, Commander Neil Armstrong emerged from the Lunar Module and took his famous first step onto the moon’s surface. He declared, “That’s one small step for man, one giant leap for mankind.” It was a monumental moment in human history", 10),
//            Arguments.of(listOf("apollo", "space", "mission"), "I am going to NARRATE a story about the Apollo 11 space mission", "It was July 21, 1969, and Neil Armstrong awoke with a start. It was the day he would become the first human being to ever walk on the moon. The journey had begun several days earlier, when on July 16th, the Apollo 11 launched from Earth headed into outer space. On board with Neil Armstrong were Michael Collins and Buzz Aldrin. The crew landed on the moon in the Sea of Tranquility a day before the actual walk. Upon Neil’s first step onto the moon’s surface, he declared, “That’s one small step for man, one giant leap for mankind.” It sure was!", 10),
//            Arguments.of(listOf("room"), "I am going to explain the PROCESS of cleaning and organizing your room.", "Here is the perfect system for cleaning your room. First, move all of the items that do not have a proper place to the center of the room. Get rid of at least five things that you have not used within the last year. Take out all of the trash, and place all of the dirty dishes in the kitchen sink. Now find a location for each of the items you had placed in the center of the room. For any remaining items, see if you can squeeze them in under your bed or stuff them into the back of your closet. See, that was easy", 10),
//            Arguments.of(listOf("ocean", "lake"), " I am going to COMPARE and CONTRAST an ocean and a lake.", "Oceans and lakes have much in common, but they are also quite different. Both are bodies of water, but oceans are very large bodies of salt water, while lakes are much smaller bodies of fresh water. Lakes are usually surrounded by land, while oceans are what surround continents. Both have plants and animals living in them. The ocean is home to the largest animals on the planet, whereas lakes support much smaller forms of life. When it is time for a vacation, both will make a great place to visit and enjoy.", 10),
////            Arguments.of(listOf("critique", "blue", "whales, baseball", "game"), " I am going to CRITIQUE the Blue Whales’ first baseball game of the new season.", "The Blue Whales just played their first baseball game of the new season; I believe there is much to be excited about. Although they lost, it was against an excellent team that had won the championship last year. The Blue Whales fell behind early but showed excellent teamwork and came back to tie the game. The team had 15 hits and scored 8 runs. That’s excellent! Unfortunately, they had 5 fielding errors, which kept the other team in the lead the entire game. The game ended with the umpire making a bad call, and if the call had gone the other way, the Blue Whales might have actually won the game. It wasn’t a victory, but I say the Blue Whales look like they have a shot at the championship, especially if they continue to improve.", 10),
//            Arguments.of(listOf("neighbors", "ticket", "school", "fair"), " I am going to PERSUADE my neighbors to buy tickets to the school fair", "The school fair is right around the corner, and tickets have just gone on sale. We are selling a limited number of tickets at a discount, so move fast and get yours while they are still available. This is going to be an event you will not want to miss! First off, the school fair is a great value when compared with other forms of entertainment. Also, your ticket purchase will help our school, and when you help the school, it helps the entire community. But that’s not all! Every ticket you purchase enters you in a drawing to win fabulous prizes. And don’t forget, you will have mountains of fun because there are acres and acres of great rides, fun games, and entertaining attractions! Spend time with your family and friends at our school fair. Buy your tickets now!", 10),
//            Arguments.of(listOf("logical", "argument", "neighbor", "school", "fair"), "I am going to present a logical ARGUMENT as to why my neighbor should attend the school fair.", "The school fair is right around the corner, and tickets have just gone on sale. Even though you may be busy, you will still want to reserve just one day out of an entire year to relax and have fun with us. Even if you don’t have much money, you don’t have to worry. A school fair is a community event, and therefore prices are kept low. Perhaps, you are still not convinced. Maybe you feel you are too old for fairs, or you just don’t like them. Well, that’s what my grandfather thought, but he came to last year’s school fair and had this to say about it: “I had the best time of my life!” While it’s true that you may be able to think of a reason not to come, I’m also sure that you can think of several reasons why you must come.  We look forward to seeing you at the school fair!", 10),
//            Arguments.of(listOf("ade"), "Pineapple fruits", "Each pineapple fruit weighs about 2.5kg", 10),
//            Arguments.of(listOf(""), "Maize Crop", "Maize crop is good for food and energy", 10),
//            Arguments.of(listOf(""), "Basket of Apples", "Each basket contains about 70 apple fruits", 10),
//            Arguments.of(listOf(""), "Basket of Mangoes", "Basket of mangoes contain average of 65 mango fruits", 10),
//            Arguments.of(listOf(""), "Basket of Fruits", "Basket of orange contains more than 80 orange fruits", 10),
            Arguments.of(listOf(""), "Giant African Land Snail (GALS)", "GALS are good source of animal protein", 10),
//            Arguments.of(listOf(""), "", "", 10),
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

        assertTrue(actual.size <= limit, "Within Limit")
        assertEquals(expected, actual)

//        assertTrue(actual.containsAll(expected), "All expected entries are present")

        val actualSet = actual.toSet()
        assertEquals(actualSet.size, actual.size, "Has no duplicate")


    }
}
