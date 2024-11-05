package com.example.chatter

import kotlinx.serialization.Serializable

@Serializable
object SigIn

@Serializable
object Singup

@Serializable
object HomeScreen

@Serializable
data class ChatScreen(val id:String)


@Serializable
object GroupsList

@Serializable
data class GroupChats(val id: String)