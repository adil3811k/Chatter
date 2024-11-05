package com.example.chatter.group.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.modul.Group
import com.example.chatter.modul.GroupsChat
import com.example.chatter.modul.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class GroupUiState(
    val list: List<Group> = emptyList<Group>(),
    val groupchats: List<GroupsChat> = emptyList<GroupsChat>()
)

class GroupViewModel: ViewModel() {

    private val _state = MutableStateFlow(GroupUiState())
    val state = _state
        .onStart {
            attachLListener()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            GroupUiState()
        )


    private fun attachLListener(){
        val uid = FirebaseAuth.getInstance().uid?:""
        Firebase.firestore.collection("Users")
            .document(uid)
            .addSnapshotListener{snapshot, error->
                if (error!=null){
                    Log.d("Firebase", error.message?:"Error is null")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        viewModelScope.launch{
                            getGroupFormString(user.group)
                        }
                    }
                }
            }
    }

    private suspend fun   getGroupFormString(list: List<String>){
        val newGroup = mutableListOf<Group>()
            list.forEach {
                val group = Firebase.firestore.collection("Group")
                    .document(it).get().await().toObject(Group::class.java)
                group?.let { element -> newGroup.add(element) }
            }
        _state.update {
            it.copy(
                list = newGroup
            )
        }
        }
    fun sendMessage(text: String, uid: String){

    }

    fun setChatListener(id: String){

    }

    fun joinGroup(groupId: String){
        viewModelScope.launch{
            val currentUser = FirebaseAuth.getInstance().uid.getUserByUid()
            val list: MutableList<String> = currentUser?.group?:mutableListOf()
            list.add(groupId)
            Firebase.firestore.collection("Users")
                .document(currentUser?.uid?:"")
                .set(currentUser?:User())
        }
    }

    fun createGroup(Tital: String , Discription: String){
        val id = Firebase.firestore.collection("Group").document().id
        val group = Group(
            title =  Tital,
            description = Discription,
            groupId = id,
            owner = FirebaseAuth.getInstance().uid,
            createdAt = Timestamp.now()
        )
        Firebase.firestore.collection("Group")
            .document(id)
            .set(group)
        joinGroup(id)
    }
    private suspend fun String?.getUserByUid(): User?{
        if (this!=null){
            val user =  Firebase.firestore.collection("Users")
                .document(this)
                .get().await()
                .toObject(User::class.java)
            return user
        }else{
            return null
        }
    }
}