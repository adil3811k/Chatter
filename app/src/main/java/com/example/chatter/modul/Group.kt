package com.example.chatter.modul

import com.google.firebase.Timestamp

data class Group(
    val title : String? = null,
    val description: String? = null,
    val groupId : String?= null,
    val owner: String? = null,
    val createdAt: Timestamp? = null
)