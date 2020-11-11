package com.ilatyphi95.farmersmarket.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.Product

fun addToRecent(product: Product) {
    val user = FirebaseAuth.getInstance().currentUser!!
    val fireStoreRef = FirebaseFirestore.getInstance().collection("users/${user.uid}/recent")
    fireStoreRef.document(product.id).set(AdItem(
        itemId = "",
        name = product.name,
        price = product.priceStr,
        quantity = product.qtyAvailable,
        imageUrl = product.imgUrls.getOrElse(0){""}
    ), SetOptions.merge())

}