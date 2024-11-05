package com.example.chatter.profile.viewmodel

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.auth.ui.AuthActivity
import com.example.chatter.modul.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _user = MutableStateFlow<User>(User())
    val user = _user.onStart {
        getUser()
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), User()
    )


    fun logout(context: ComponentActivity) {
        try {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "User log out", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, AuthActivity::class.java))
            context.finish()
        } catch (e: Exception) {
            Toast.makeText(context, "Fail to logout ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun changeName(NewName: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "no uid found"
        val user = _user.value.copy(
            name = NewName
        )
        Firebase.firestore.collection("Users")
            .document(uid)
            .set(user)
    }


    private fun getUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "no uid found"
        Firebase.firestore.collection("Users")
            .document(uid)
            .addSnapshotListener { snapshort, error ->
                if (error != null) {
                    Log.d("Firebase", "Listener remove reason ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshort != null && snapshort.exists()) {
                    val Newuser = snapshort.toObject(User::class.java)
                    if (Newuser != null) {
                        _user.value = Newuser
                    } else {
                        _user.value = User()
                    }
                }
            }
    }
}