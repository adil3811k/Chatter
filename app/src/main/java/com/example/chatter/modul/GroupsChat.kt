package com.example.chatter.modul

import com.google.firebase.Timestamp

data class GroupsChat(
    val senderName : String? = null,
    val senderID:  String? = null,
    val text: String? = null,
    val createdAt  : Timestamp = Timestamp.now()
)