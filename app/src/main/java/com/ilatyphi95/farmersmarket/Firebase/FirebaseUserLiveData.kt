package com.ilatyphi95.farmersmarket.Firebase

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


 // Class observes the current FirebaseUser. If no user is logged in,
 // FirebaseUser will be null

class FirebaseUserLiveData : LiveData<FirebaseUser?>() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        value = firebaseAuth.currentUser
    }

    //When this object has an active observer
    //start observing the FirebaseAuthState to see if
    //there is currently a user logged in

    override fun onActive() {
        super.onActive()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    //When this object no longer has an observer
    //stop observing the FirebaseAuthState if
    //there is currently no user logged in

    override fun onInactive() {
        super.onInactive()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}