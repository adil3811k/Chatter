package com.example.chatter.modul

import com.google.firebase.Timestamp

data class Message(
    val sandBy: String? = null,
    val text: String? = null,
    val createdAt: Timestamp = Timestamp.now()
)