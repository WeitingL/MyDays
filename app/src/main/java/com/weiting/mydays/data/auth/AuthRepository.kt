package com.weiting.mydays.data.auth

import android.content.Context
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signInWithGoogle(context: Context, webClientId: String): FirebaseUser
    fun signOut()
}
