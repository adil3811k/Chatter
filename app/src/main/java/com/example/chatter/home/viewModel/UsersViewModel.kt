package com.example.chatter.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chatter.modul.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor() : ViewModel() {

    val users = Firebase.firestore.collection("Users")

    private val _state  = MutableStateFlow<List<User>>(emptyList())
    val state = _state.asStateFlow()

    init {
        addListner()
    }
    private fun addListner(){
        users.whereNotEqualTo("uid" , FirebaseAuth.getInstance().currentUser?.uid?:"")
            .addSnapshotListener{snapshot, error->
                if (error!=null){
                    Log.d("Firebase FireStore", error.message.toString())
                    return@addSnapshotListener
                }
                val userList  = mutableListOf<User>()
                snapshot?.documents?.forEach {
                    val user = it.toObject(User::class.java)!!
                    userList.add(user)
                }
                _state.value = userList
            }
    }
}