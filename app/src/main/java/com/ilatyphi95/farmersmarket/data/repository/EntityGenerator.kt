package com.ilatyphi95.farmersmarket.data.repository

import com.ilatyphi95.farmersmarket.data.entities.MyLocation
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.thedeanda.lorem.LoremIpsum
import kotlin.random.Random

object ProductGenerator {
    private val imagesUrl = listOf(
        "https://res.cloudinary.com/mikeattara/image/upload/v1596700848/Top-learner.png",
        "https://www.eatforhealth.gov.au/sites/default/files/images/the_guidelines/fruit_selection_155265101_web.jpg",
        "https://previews.123rf.com/images/usersam2007/usersam20071709/usersam2007170900013/87241670-pair-of-ripe-red-and-green-apple-fruits-with-apple-leaf-isolated-on-white-background-.jpg",
        "https://res.cloudinary.com/mikeattara/image/upload/v1596700848/Top-learner.png",
        "https://www.eatforhealth.gov.au/sites/default/files/images/the_guidelines/fruit_selection_155265101_web.jpg",
        "https://previews.123rf.com/images/usersam2007/usersam20071709/usersam2007170900013/87241670-pair-of-ripe-red-and-green-apple-fruits-with-apple-leaf-isolated-on-white-background-.jpg"
    )

    private var productList: List<Product>? = null
    private val lorem = LoremIpsum.getInstance()

    fun getList(): List<Product> {
        if (productList == null) {
            productList = generateList(10)
        }
        return productList!!
    }

    private fun generateList(count: Int): List<Product> {
        val buffer = mutableListOf<Product>()

        for (i in 1..count) {
            buffer.add(
                Product(
                    id = lorem.name,
                    name = lorem.getWords(5),
                    description = lorem.getParagraphs(3, 5),
                    sellerId = lorem.name,
                    type = "Livestock",
                    imgUrls = generateList(),
                    qtyAvailable = Random.nextInt(30),
                    qtySold = Random.nextInt(10, 20),
                    priceStr = "USD-${Random.nextInt(50, 250)}",
                    location = generateLocation()
                )
            )
        }
        return buffer.toList()
    }

    fun resetList(count: Int): List<Product> {
        productList = null
        generateList(count)
        return getList()
    }

    private fun generateList(): List<String> {
        val list = mutableSetOf<String>()
        val count = Random.nextInt(1, imagesUrl.size - 1)

        for (i in 1..count) {
            list.add(generateImage())
        }

        return list.toList()
    }


    fun generateImage(): String {

        val until = imagesUrl.size - 1
        return imagesUrl[Random.nextInt(0, until)]
    }

    fun createLocation(lat: Double, lng: Double, accuracy: Float = 3.0f): MyLocation {

        return MyLocation(
            accuracy = accuracy,
            latitude = lat,
            longitude = lng,
            time = System.currentTimeMillis() + Random.nextLong(-3000000000, 0)
        )
    }

    fun generateLocation(): MyLocation {
        return createLocation(
            Random.nextDouble(-29.0460, 36.8592),
            Random.nextDouble(-2.460415, 37.297204)
        )
    }
}