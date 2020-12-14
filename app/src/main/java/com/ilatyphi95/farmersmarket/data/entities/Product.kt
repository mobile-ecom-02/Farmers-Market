package com.ilatyphi95.farmersmarket.data.entities

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

/**
 *
 * @param id sequence of characters unique
 * @param name name of the product
 * @param description detail description of the product
 * @param sellerId sellers identification key unique sequence of characters
 * @param type classification of product, farm produce, lifestock etc.
 * @param imgUrls Urls for all images uploaded for the product
 * @param qtyAvailable number of available products
 * @param qtySold total number of items that has ever been sold
 * @param priceStr concatenation of CurrencyCode(Three Letters) and Amount(to Currency Decimal places)
 */

@Parcelize
data class Product @JvmOverloads constructor(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val sellerId: String = "",
    val type: String = "",
    val imgUrls: List<String> = emptyList(),
    var qtyAvailable: Int = 0,
    var qtySold: Int = 0,
    val priceStr: String = "USD-0",
    val location: MyLocation? = null,
    val keywords: List<String> = emptyList()
) : Parcelable