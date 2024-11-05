package com.example.chatter.home.viewModel

import android.util.Log
import androidx.compose.animation.core.snap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatter.modul.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class ChatViewmodel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<List<Message>>(emptyList())
    val state = _state.asStateFlow()


    fun sendMessage(text: String, uid: String) {
        val myUid = FirebaseAuth.getInstance().uid?:""
        val collection = listOf<String>(myUid , uid).sorted().joinToString("_")
        val message = Message(myUid, text)
        Firebase.firestore.collection(collection).document().set(message).addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }.addOnSuccessListener {
                Log.d("Firebase", "Message add Success fully")
            }
    }
    fun setListner(uid: String) {
        val myUid = FirebaseAuth.getInstance().uid?:""
        val collection = listOf<String>(myUid , uid).sorted().joinToString("_")
        Firebase.firestore.collection(collection)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener {snapshort, error ->
                if (error != null) {

                    return@addSnapshotListener
                }
                val messageList = mutableListOf<Message>()
                snapshort?.documents?.forEach {
                    val message = it.toObject(Message::class.java)!!
                    Log.d("Firebase", message.sandBy?:"Send by null")
                    messageList.add(message)
                }
                _state.value = messageList
            }

    }
}