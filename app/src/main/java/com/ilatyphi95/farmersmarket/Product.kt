package com.ilatyphi95.farmersmarket

import com.ilatyphi95.farmersmarket.utils.stringToMoney

/**
 *
 * @param id sequence of characters unique
 * @param name name of the product
 * @param sellerId sellers identification key unique sequence of characters
 * @param type classification of product, farm produce, lifestock etc.
 * @param imgUrls Urls for all images uploaded for the product
 * @param qtyAvailable number of available products
 * @param qtySold total number of items that has ever been sold
 * @param priceStr concatenation of CurrencyCode(Three Letters) and Amount(to Currency Decimal places)
 */
 data class Product @JvmOverloads constructor(
    val id: String = "",
    val name: String,
    val sellerId: String,
    val type: String,
    val imgUrls: List<String> = emptyList(),
    var qtyAvailable: Int,
    var qtySold: Int,
    val priceStr: String = "USD-0"
) {
    val price = stringToMoney(priceStr)
}