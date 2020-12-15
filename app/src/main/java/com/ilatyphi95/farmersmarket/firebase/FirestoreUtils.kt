package com.ilatyphi95.farmersmarket.firebase

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.Product

fun addToInterested(product: Product) {
    val user = FirebaseAuth.getInstance().currentUser!!
    val fireStoreRef = FirebaseFirestore.getInstance().collection("users/${user.uid}/interestedItems")
    fireStoreRef.document(product.id).set(AdItem(
        itemId = "",
        name = product.name,
        price = product.priceStr,
        quantity = product.qtyAvailable,
        imageUrl = product.imgUrls.getOrElse(0){""}
    ), SetOptions.merge())
}

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

fun Query.addSnapshotListener(lifecycleOwner: LifecycleOwner, listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
    val registration = addSnapshotListener(listener)
    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            registration.remove()
            lifecycleOwner.lifecycle.removeObserver(this)
        }
    })
}


fun DocumentReference.addSnapshotListener(owner: LifecycleOwner, listener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit): ListenerRegistration {
    val registration = addSnapshotListener(listener)

    owner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            registration.remove()
            owner.lifecycle.removeObserver(this)
        }
    })

    return registration
}


fun CollectionReference.addSnapshotListener(owner: LifecycleOwner, listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit): ListenerRegistration {
    val registration = addSnapshotListener(listener)

    owner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            registration.remove()
            owner.lifecycle.removeObserver(this)
        }
    })

    return registration
}
