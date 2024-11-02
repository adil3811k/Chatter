package com.example.chatter.modul

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties


@IgnoreExtraProperties
data class User (
    val name  : String? = null,
    val uid: String? = null,
    val createdAt: Timestamp? = null,
    val email: String? = null,
    val group: List<String>? = emptyList()
)